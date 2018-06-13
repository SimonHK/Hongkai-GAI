package com.graphai.job;

import com.graphai.dao.*;
import com.graphai.model.*;
import com.graphai.util.*;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * 任务Handler示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHandler(value = "importdbJob")
@Component
public class ImportdbJob extends IJobHandler {
    @Resource
    private XxlJobInfoDao xxlJobInfoDao;
    @Resource
    private TestDao testDao;
    @Resource
    private StoreresoultDao storeresoultDao;
    @Resource
    private EventlibrarynewsDao eventlibrarynewsDao;
    @Resource
    private LocationsDao locationsDao;

    //数据来源默认ds0
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        boolean empty = StringUtils.isEmpty(param);
        if (!empty) {
            //查询出nlp初处理库结果
            List<Storeresult> storeResultData  = findStoreResultData(param);

            //将结果进行NLP的机构过滤分析
            List<Eventlibrarynews> evnews = new ArrayList<>(0);
            Eventlibrarynews en = null;
            //查询已有的机构内容
            Page locationsInfoPage = new Page();
            int pageIndex=1;
            do {
                locationsInfoPage = getLocationsInfoByDymanic(pageIndex);
                pageIndex = locationsInfoPage.getNextPage();
                /*System.out.println("当前页是："+locationsInfoByDymanic.getPageIndex());
                System.out.println("下一页是："+locationsInfoByDymanic.getNextPage());
                System.out.println("上一页是："+locationsInfoByDymanic.getPrePage());
                System.out.println("总记录数："+locationsInfoByDymanic.getRecord());
                System.out.println("总页数是："+locationsInfoByDymanic.getTotalPageCount());
                System.out.println("页大小是："+locationsInfoByDymanic.getPageSize());*/
                List<Locations> locationsList = locationsInfoPage.getLocationsList();
                /*for (Locations locations : locationsList) {
                    System.out.print("机构名称："+locations.getLocationname());
                    System.out.print(";");
                }*/
                for (Storeresult store : storeResultData) {
                    //实体名称需要分析
                    String ventcontent = store.getVentcontent();//待分析内容
                    //StringBuffer sb  = new StringBuffer("");//定义一个机构或实体字符串
                    //for(Locations locs : locationsData){
                    //String locationname = locs.getLocationname();//实体名
                    //String locationalias = locs.getLocationalias();//实体别名
                    List<AnalysisResult> analysisResultList = GraphAiUtils.ruleformles(locationsList, "nt", ventcontent);
                    /*Iterator<AnalysisResult> i = analysisResultList.iterator();
                    analysisResultList = new ArrayList<>(0);
                    while (i.hasNext()) {
                        //System.out.println(i.next());
                        analysisResultList.add(i.next());
                        //sb.append(i.next()).append(",");
                    }*/
                    //List<AnalysisResult> unique =  analysisResultList.stream().distinct().collect(Collectors.toList());

                  /*  List<AnalysisResult> unique = analysisResultList.stream().collect(
                            collectingAndThen(
                                    toCollection(() -> new TreeSet<>(comparingLong(AnalysisResult::getLocationids()))), ArrayList::new)
                    );*/
                    //对象去重复
                    Set<String> nameSet = new HashSet<>();
                    List<AnalysisResult> analysisResultsDistinctByLocationids = analysisResultList.stream()
                            .filter(e -> nameSet.add(e.getLocationids()))
                            .collect(Collectors.toList());

                    if(analysisResultsDistinctByLocationids.size() > 0) {
                        for (AnalysisResult analysisRes : analysisResultsDistinctByLocationids) {
                            en = new Eventlibrarynews();
                            String uuid = UuidUtil.get32UUID();
                            en.setEventid(uuid);
                            en.setOriginallink(store.getDatasource());
                            en.setEventtype(store.getEventclassification());
                            String nowTime = TimeTools.getNowTime();
                            en.setEtime(nowTime);
                            en.setEntityname(analysisRes.getAnalysisWord());
                            //人名需要分析
                            en.setPersoname(analysisRes.getSentencePersonName());
                            //实体机构ID需要依托实体名称分析后的结果进行查询并取出机构ID
                            en.setIcid(analysisRes.getLocationids());
                            //事件发生事件
                            en.setEventtime("发表时间:[" + store.getTimeofoccurrence() + "],内容包含时间:[" + analysisRes.getSentenceTimes() + "]");
                            en.setEntitycontent(store.getVentcontent());
                            en.setAbstracttext(store.getAbstracttext());

                            //将机构和人员内容进行拼装入库
                            saveEventLibraryNews(en);
                            //evnews.add(en);
                            XxlJobLogger.log("==============================================================");
                            //XxlJobLogger.log("*  入库事件ID：[" + uuid + "]");
                            //XxlJobLogger.log("*  入库事件来源：[" + store.getDatasource() + "]");
                            XxlJobLogger.log("*  入库事件类型：[" + store.getEventclassification() + "]");
                            //XxlJobLogger.log("*  入库事件时间：[" + nowTime + "]");
                            XxlJobLogger.log("*  实体或机构：[" + analysisRes.getAnalysisWord() + "]");
                            //XxlJobLogger.log("*  事件包含人名：[" + analysisRes.getSentencePersonName() + "]");
                            XxlJobLogger.log("*  事件实体机构ID：[" + analysisRes.getLocationids() + "]");
                            //XxlJobLogger.log("*  事件摘要：[" + store.getAbstracttext() + "]");
                            //XxlJobLogger.log("*  时间：发表时间:[" + store.getTimeofoccurrence() + "],内容包含时间:[" + analysisRes.getSentenceTimes() + "]");
                            XxlJobLogger.log("==============================================================");
                        }
                    }
                    // }
                }
            }while (locationsInfoPage.getPageIndex() != locationsInfoPage.getTotalPageCount());

        } else {
            XxlJobLogger.log("请输入要处理数据的日期时间如：2018-06-08");
        }

        return SUCCESS;
    }

    //获取NLP已经初步分析的结果
    public List<Storeresult> findStoreResultData() {
        XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        List<Storeresult> storeresults = storeresoultDao.pageList();
        XxlJobLogger.log("查询NLP以分析后初步内容【" + storeresults.size() + "】");
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return storeresults;
    }

    //从语料库中获取语料
    public List<Storeresult> findStoreResultData(String param) {
        XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        List<Storeresult> storeresults = new ArrayList<Storeresult>(0);
        if (!StringUtils.isEmpty(param)) {
            if (TimeTools.isValidDate(param)) {
                storeresults = storeresoultDao.findPageListByTime(param);
            }
        } else {
            storeresults = storeresoultDao.pageList();
        }
        XxlJobLogger.log("查询NLP以分析后初步内容【" + storeresults.size() + "】");
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return storeresults;
    }


    //获取机构地域信息
    public List<Locations> findLocationsData() {
        XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        List<Locations> locations = locationsDao.pageList();
        XxlJobLogger.log("查询机构即地域内容【" + locations.size() + "】");
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return locations;
    }

    //获取机构总数
    public int countLocationsData() {
        XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        int l = locationsDao.locationCount();
        XxlJobLogger.log("查询机构即地域数量【" + l + "】");
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return l;
    }

    //获取机构地域信息
    public List<Locations> findLocationsData(int stratRow,int endRow ) {
        XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        List<Locations> locations = locationsDao.getlocationCountByDymanic(stratRow,endRow);
        XxlJobLogger.log("查询机构即地域内容【" + locations.size() + "】");
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return locations;
    }


    //数据存储入事件库中
    public int saveEventLibraryNews(List<Eventlibrarynews> eventlibrarynews) {
        int save = 0;
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        for (Eventlibrarynews eventlibrarynews1 : eventlibrarynews) {
            save = eventlibrarynewsDao.save(eventlibrarynews1);
        }
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return save;
    }

    //数据存储入事件库中
    public int saveEventLibraryNews(Eventlibrarynews eventlibrarynews) {
        int save = 0;
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        //for (Eventlibrarynews eventlibrarynews1 : eventlibrarynews) {
            save = eventlibrarynewsDao.save(eventlibrarynews);
        //}
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return save;
    }

    public void restoreDefaultDataSource() {
        String dbType = DataSourceContextHolder.getDbType();
        if (!"ds0".equals(dbType)) {
            DataSourceContextHolder.setDbType("ds0");
        }
    }



    /**
     * @return
     */
    public Page getLocationsInfoByDymanic(int pageIndex) {
        Page page=new Page();
        page.setPageIndex(pageIndex);      //当前页
        int reCount= countLocationsData();
        page.setRecord(reCount);           //总记录数
        HashMap parMap=new HashMap();
        parMap.put("stratRow",page.getSartRow());
        parMap.put("endRow",page.getEndRow());
        try {
            List<Locations> locationsData = findLocationsData(page.getSartRow(),page.getEndRow());
            page.setLocationsList(locationsData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }
    //分页查询机构地域内容
    public Page getLocationsInfoByDymanic(Locations locations,int pageIndex) {
        Page page=new Page();
        page.setPageIndex(pageIndex);      //当前页
        int reCount= countLocationsData();
        page.setRecord(reCount);           //总记录数
        List<Locations> houseList=new ArrayList<Locations>();
        HashMap parMap=new HashMap();
        parMap.put("locationname",locations.getLocationname());
        parMap.put("locationid",locations.getLocationid());
        parMap.put("locationalias", locations.getLocationalias());
        parMap.put("stratRow",page.getSartRow());
        parMap.put("endRow",page.getEndRow());
        try {
            List<Locations> locationsData = findLocationsData(page.getSartRow(),page.getEndRow());
            page.setLocationsList(locationsData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    //测试分页查询效果
    public void test(){
        int pageIndex=1;
        Page locationsInfoByDymanic = getLocationsInfoByDymanic(pageIndex);
        System.out.println("当前页是："+locationsInfoByDymanic.getPageIndex());
        System.out.println("下一页是："+locationsInfoByDymanic.getNextPage());
        System.out.println("上一页是："+locationsInfoByDymanic.getPrePage());
        System.out.println("总记录数："+locationsInfoByDymanic.getRecord());
        System.out.println("总页数是："+locationsInfoByDymanic.getTotalPageCount());
        System.out.println("页大小是："+locationsInfoByDymanic.getPageSize());
        List<Locations> locationsList = locationsInfoByDymanic.getLocationsList();
        for (Locations locations : locationsList) {
            System.out.print("机构名称："+locations.getLocationname());
            System.out.print(";");
        }
    }

}

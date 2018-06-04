package com.graphai.job;

import com.graphai.dao.*;
import com.graphai.model.*;
import com.graphai.util.DataSourceContextHolder;
import com.graphai.util.GraphAiUtils;
import com.graphai.util.TimeTools;
import com.graphai.util.UuidUtil;
import com.mysql.jdbc.TimeUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


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

        } else {
            //查询出nlp初处理库结果
            List<Storeresult> storeResultData = findStoreResultData();
            //将结果进行NLP的机构过滤分析
            List<Eventlibrarynews> evnews = new ArrayList<>(0);
            Eventlibrarynews en = null;
            //查询已有的机构内容
            List<Locations> locationsData = findLocationsData();
            //定义处理后要存储的内容
            for (Storeresult store : storeResultData) {
                //实体名称需要分析
                String ventcontent = store.getVentcontent();//待分析内容
                //StringBuffer sb  = new StringBuffer("");//定义一个机构或实体字符串
                //for(Locations locs : locationsData){
                //String locationname = locs.getLocationname();//实体名
                //String locationalias = locs.getLocationalias();//实体别名
                List<AnalysisResult> analysisResultList = GraphAiUtils.ruleformles(locationsData, "nt", ventcontent);
                for (AnalysisResult analysisRes : analysisResultList) {
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
                    evnews.add(en);
                    XxlJobLogger.log("==============================================================");
                    XxlJobLogger.log("*  入库事件ID：[" + uuid + "]");
                    XxlJobLogger.log("*  入库事件来源：[" + store.getDatasource() + "]");
                    XxlJobLogger.log("*  入库事件类型：[" + store.getEventclassification() + "]");
                    XxlJobLogger.log("*  入库事件时间：[" + nowTime + "]");
                    XxlJobLogger.log("*  实体或机构：[" + analysisRes.getAnalysisWord() + "]");
                    XxlJobLogger.log("*  事件包含人名：[" + analysisRes.getSentencePersonName() + "]");
                    XxlJobLogger.log("*  事件实体机构ID：[" + analysisRes.getLocationids() + "]");
                    XxlJobLogger.log("*  事件摘要：[" + store.getAbstracttext() + "]");
                    XxlJobLogger.log("*  时间：发表时间:[" + store.getTimeofoccurrence() + "],内容包含时间:[" + analysisRes.getSentenceTimes() + "]");
                    XxlJobLogger.log("==============================================================");
                }
                // }
            }
            //将机构和人员内容进行拼装入库
            int i = saveEventLibraryNews(evnews);
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

    public void restoreDefaultDataSource() {
        String dbType = DataSourceContextHolder.getDbType();
        if (!"ds0".equals(dbType)) {
            DataSourceContextHolder.setDbType("ds0");
        }
    }

}

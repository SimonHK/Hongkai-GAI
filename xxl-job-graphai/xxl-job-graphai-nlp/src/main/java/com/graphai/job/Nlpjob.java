package com.graphai.job;

import com.alibaba.druid.util.StringUtils;
import com.graphai.dao.*;
import com.graphai.model.*;
import com.graphai.util.DataSourceContextHolder;
import com.graphai.util.GraphAiUtils;
import com.graphai.util.TimeTools;
import com.graphai.util.UuidUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.occurrence.Occurrence;
import com.hankcs.hanlp.corpus.occurrence.PairFrequency;
import com.hankcs.hanlp.corpus.occurrence.TermFrequency;
import com.hankcs.hanlp.corpus.occurrence.TriaFrequency;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 任务Handler示例（Bean模式）
 *
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHandler(value="nlpJob")
@Component
public class Nlpjob extends IJobHandler {
    @Resource
    private LawdregDao lawdregDao;

    @Resource
    private NlpruleDao nlpruleDao;

    @Resource
    private IenvironmentDao ienvironmentDao;

    @Resource
    private CrawlercontentDao crawlercontentDao;

    @Resource
    private StoreresoultDao storeresoultDao;

    /*@Resource
    private NaturalenvDao naturalenvDao;*/

   /* @Resource
    private IndustrysituationDao industrysituationDao;

    @Resource
    private NationalpolicyDao nationalpolicyDao;

    @Resource
    private PubEnvironmentDao pubEnvironmentDao;
*/
    @Override
    public ReturnT<String> execute(String param) throws Exception {

        /**获取要分析的数据*/
        List<Crawlercontent> data = findData(param);
        XxlJobLogger.log("当前获取准备分析的语料为：【"+data.size()+"】条");

        /**获取规则内容*/
        List<Nlprule> nlprules = findNlprule(param);
        XxlJobLogger.log("准备用于分析语料的规则数量为：【"+nlprules.size()+"】条");
        /**
        //开始分析根据语料的切分段落后获取的的内容进行nlp分析，参数 sentenceCont 是摘要的数量，
        // 如果是一段内容摘要的数量可以设置为1，
        //如果语料为一片文章可以多设几条摘要信息。
        */
        List<RulePressObject> ruleformles = GraphAiUtils.ruleformles(nlprules, data,3);

        XxlJobLogger.log("满足规则的语料为：【"+ruleformles.size()+"】条");

        /**处理分析结果*/
        List<RulePressObject> ls = getAnalysis(ruleformles);
        if(ls.size()>0) {
            /**将结果入库*/
            int i = inDbOperation(ls);
        }
        XxlJobLogger.log("符合规则的内容一共有：【"+ls.size()+"】条！");
        return SUCCESS;
    }

    private List<RulePressObject> getAnalysis(List<RulePressObject> ruleformles) {
        List<RulePressObject> ls = new ArrayList<>(0);
        for(RulePressObject rulePressObject:ruleformles){
            Boolean isStore = Boolean.valueOf(rulePressObject.getType());
            if(isStore){
                ls.add(rulePressObject);
            }
        }
        return ls;
    }

    private int inDbOperation(List<RulePressObject> ls) {
        int inDb = 0;
        for (RulePressObject rPressObject:ls) {
            Storeresult storeresult = new Storeresult();
            storeresult.setVentcontent(rPressObject.getTextContent());
            storeresult.setDatasource(rPressObject.getTextFrom());
            storeresult.setTimeofoccurrence(rPressObject.getTextTime());
            storeresult.setEventclassification(rPressObject.getRuleType());
            storeresult.setAbstracttext(rPressObject.getAbstractText());
            storeresult.setLocation("");
            String nowTime = TimeTools.getNowTime();
            storeresult.setIndbtime(nowTime);
            inDb = saveStoreresult(storeresult);
            XxlJobLogger.log("入库时间是：【"+nowTime+"】");
            XxlJobLogger.log("内容时间是：【"+rPressObject.getTextTime()+"】");
            XxlJobLogger.log("在规则《"+rPressObject.getRuleType()+"》的公式：【"+rPressObject.getRuleText()+"】");
            XxlJobLogger.log("符合分析计算结果为《"+rPressObject.getType()+"》");
            XxlJobLogger.log("内容摘要：【"+rPressObject.getAbstractText()+"】");
            XxlJobLogger.log("入库内容是：【"+rPressObject.getTextContent()+"】");
        }
        return inDb;
    }

    /**
     * @param stringCase 待分析内容
     * @param typeName 分析后属于类别
     * @param typekey 将要插入自定义字典库的词
     * */
    private void procceWordForCRF(String[] stringCase,String typeName,String[] typekey,String rulename) {
        Segment segment = new CRFSegment().enableCustomDictionary(true).enableOrganizationRecognize(true).enableNameRecognize(true);

        for(int t = 0 ; t < typekey.length;t++){
            CustomDictionary.insert(typekey[t]);
        }
        //句子级别相似度添加关键词入字典分析库
        //提供之前处理
        List<Store> stores = new ArrayList<>(0);
        for (String sentence : stringCase)
        {
            List<Term> termList = segment.seg(GraphAiUtils.filterString(sentence));
            String trr = GraphAiUtils.filterString(sentence.toString().trim());
            //System.out.println(termList);
            for(int tt  = 0 ; tt < typekey.length;tt++){
                String key = GraphAiUtils.filterString(typekey[tt]);
                List<Term> seg = segment.seg(key);
                Term term = seg.get(0);
                String keynature = term.nature.toString();String keyword = term.word;
                for(int i  = 0 ; i < termList.size(); i++){
                    String word = termList.get(i).word;//词
                    String nature = termList.get(i).nature.toString();//词性
                    if(trr.indexOf(word)>0){
                        Store store = new Store();
                        store.setKeyWord(keyword);
                        store.setContent(sentence);
                        stores.add(store);
                        System.out.println(sentence);
                        XxlJobLogger.log("第二种方法根据【"+rulename+"】大类中的规则关键词【"+keyword+"】分析的内容是：{"+trr+"}");
                    }
                }
            }
            /*List<Term> seg = segment.seg(trim);
            StringBuffer sb=new StringBuffer();
            for(Term item:seg){
                sb.append(GraphAiUtils.filterString(item.word));
            }
            XxlJobLogger.log("类别：【"+typeName+"】处理内容为：【"+sb.toString()+"】");*/
            /*if(trim.indexOf("蝶应智能") > 0){
                strings.add(trim);
            }*/
        }
        List<Store> stores1 = GraphAiUtils.ifRepeat(stores);
        for(int q = 0 ; q <stores1.size();q++){
            Store store = stores1.get(q);
            String keyWord = store.getKeyWord();
            String content = store.getContent();;
            XxlJobLogger.log("关键字【"+keyWord+"】处理后符合的段落是：【"+GraphAiUtils.filterString(content)+"】");
        }

        /*boolean empty = strings.isEmpty();
        if(!empty){
            for (int j  = 0 ; j < strings.size(); j++){
                System.out.println("蝶应智能"+"存在语句："+strings.get(j).toString());
            }
        }*/
    }

    private List<String> procceWord(Suggester suggester, String typekey, int num, String rulename) {
        List<String> storelist = suggester.suggest(typekey, num);
        XxlJobLogger.log("手工设置取出：【"+num+"】条符合关键词：【"+typekey+"】的事件有（ "+storelist.size()+" ）条！");
        List<String> returntext  = new ArrayList<String>(0);
        int cont = 1;
        for(int tt = 0 ; tt < storelist.size(); tt ++){
            String s = storelist.get(tt);
            XxlJobLogger.log("根据【"+rulename+"】大类中的规则关键词【"+typekey+"】分析的第<"+cont+">条内容是：{"+s+"}");
            returntext.add(s);
            cont++;
        }
        return returntext;
    }

    //组合词组
    public List<String> getZuHe(List<String> daizuhe01,List<String> daizuhe02){
        List<String> returnlis = new ArrayList<>(0);
        Map<String, String> map =  new HashMap<String, String>();
        for(int z1 = 0 ; z1 < daizuhe01.size(); z1++ ){
            String s2 = daizuhe01.get(z1);
            //做两两组合
            for(int z11 = 0 ; z11 < daizuhe02.size(); z11++){
                String concat = s2.concat(daizuhe02.get(z11));
                //returnlis.add(concat);
                //去重复处理
                map.put(concat,String.valueOf(z11));
            }
        }
        Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry<String, String> m = iter.next();
            String s = m.getKey();                // 从Map.Entry对象中取出key值
            returnlis.add(s);

        }

        return returnlis;
    }

    private void intoIenvir() {
        Ienvironment ienvironment = new Ienvironment();
        //ienvironment.setId(Integer.valueOf(UuidUtil.get32UUID()));
        ienvironment.setId(UuidUtil.get32UUID());
        ienvironment.setNlpcontent("sdfsdfsdfsdfsdf");
        ienvironment.setNlptime("2018-1-1");
        ienvironment.setNlptype("nolll");
        XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        List<Ienvironment> ienvironments = ienvironmentDao.pageList();
        int save = ienvironmentDao.save(ienvironment);
       // XxlJobLogger.log(save);
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
    }

    //从语料库中获取语料
    public List<Crawlercontent> findData(String param) {
        XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds1");     //注意这里在调用userMapper前切换到ds1的数据源
        List<Crawlercontent> crawlercontents = new ArrayList<Crawlercontent>(0);
        if(!StringUtils.isEmpty(param)) {
            if (TimeTools.isValidDate(param)) {
                crawlercontents = crawlercontentDao.findPageListByTime(param);
            } else {
                crawlercontents = crawlercontentDao.pageList();
            }
        }else{
            crawlercontents = crawlercontentDao.pageList();
        }
        XxlJobLogger.log("查询出待分析内容【"+crawlercontents.size()+"】");
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return crawlercontents;
    }

    //获取规则列表
    public List<Nlprule> findNlprule(String parm){
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        String reg = "[\\u4e00-\\u9fa5]+";
        List<Nlprule> nlprules = new ArrayList<Nlprule>(0);
        if(!StringUtils.isEmpty(parm)) {
            if (parm.matches(reg)) {
                nlprules = nlpruleDao.pageListForRuleName(parm);
            }else{
                nlprules = nlpruleDao.pageList();
            }
        }else{
            nlprules = nlpruleDao.pageList();
        }
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return nlprules;
    }

    //数据来源ds2
    public void findByIdDs2() {
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds2的数据源

        List<Lawdreg> testInfos = lawdregDao.pageList();
        System.out.println("");
    }

    //数据俩ds2
    public int saveStoreresult(Storeresult storeresults){
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        storeresults.setId(UuidUtil.get32UUID());
        int save = storeresoultDao.save(storeresults);
        restoreDefaultDataSource();
        XxlJobLogger.log("恢复默认数据源！");
        return save;
    }

    public void insertBatch(){
        long start = System.currentTimeMillis();
        List<Ienvironment> list = new ArrayList<>();
        Ienvironment ienvironment;
        for (int i = 0; i < 10000; i++) {
            ienvironment = new Ienvironment();
            ienvironment.setId(UuidUtil.get32UUID());
            ienvironment.setNlptype("type1"+i);
            ienvironment.setNlptime("102211"+i);
            ienvironment.setNlpcontent("ssdfssdfsdfsdf"+i);
            list.add(ienvironment);
        }
        try {
            XxlJobLogger.log("注切换数据源定ds2！");
            DataSourceContextHolder.setDbType("ds2");     //注意这里在调用前切换数据源
            ienvironmentDao.insertBatch(list);

        } catch (Exception e) {
            XxlJobLogger.log("批量插入数据错误！"+e.getMessage());
        }
        long end = System.currentTimeMillis();
        XxlJobLogger.log("批量插入数据用时【"+(start - end)+"】并恢复数据源！");
        restoreDefaultDataSource();//恢复数据源
    }

    public void restoreDefaultDataSource(){
        String dbType = DataSourceContextHolder.getDbType();
        if (!"ds0".equals(dbType)){
            DataSourceContextHolder.setDbType("ds0");
        }
    }
}

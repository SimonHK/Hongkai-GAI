package com.graphai.job;

import com.alibaba.druid.util.StringUtils;
import com.graphai.dao.*;
import com.graphai.model.*;
import com.graphai.util.*;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.occurrence.Occurrence;
import com.hankcs.hanlp.corpus.occurrence.PairFrequency;
import com.hankcs.hanlp.corpus.occurrence.TermFrequency;
import com.hankcs.hanlp.corpus.occurrence.TriaFrequency;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;
import com.hankcs.hanlp.tokenizer.BasicTokenizer;
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
 * <p>
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHandler(value = "nlpJob")
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
        XxlJobLogger.log("待分析的语料为：【" + data.size() + "】条");
        /**获取规则内容*/
        List<Nlprule> nlprules = findNlprule(param);
        XxlJobLogger.log("待分析规则数量：【"+nlprules.size()+"】条；开始分析......");
        /**
         //开始分析根据语料的切分段落后获取的的内容进行nlp分析，参数 sentenceCont 是摘要的数量，
         // 如果是一段内容摘要的数量可以设置为1，
         //如果语料为一片文章可以多设几条摘要信息。
         */
        List<RulePressObject> ruleformles = ruleformles(nlprules, data, 1);

        //XxlJobLogger.log("满足规则的语料为：【"+ruleformles.size()+"】条");

        /* *//**处理分析结果*//*
        List<RulePressObject> ls = getAnalysis(ruleformles);
        if(ls.size()>0) {
            *//**将结果入库*//*
            int i = inDbOperation(ls);
        }
        XxlJobLogger.log("符合规则的内容一共有：【"+ls.size()+"】条！");*/
        return SUCCESS;
    }

    private List<RulePressObject> getAnalysis(List<RulePressObject> ruleformles) {
        List<RulePressObject> ls = new ArrayList<>(0);
        for (RulePressObject rulePressObject : ruleformles) {
            Boolean isStore = Boolean.valueOf(rulePressObject.getType());
            if (isStore) {
                ls.add(rulePressObject);
            }
        }
        return ls;
    }

    private int inDbOperations(List<RulePressObject> ls) {
        int inDb = 0;
        for (RulePressObject rPressObject : ls) {
            Storeresult storeresult = new Storeresult();
            storeresult.setVentcontent(rPressObject.getTextContent());
            storeresult.setDatasource(rPressObject.getTextFrom());
            storeresult.setTimeofoccurrence(rPressObject.getTextTime());
            storeresult.setEventclassification(rPressObject.getRuletype());
            storeresult.setAbstracttext(rPressObject.getAbstractText());
            storeresult.setLocation("");
            String nowTime = TimeTools.getNowTime();
            storeresult.setIndbtime(nowTime);
            inDb = saveStoreresult(storeresult);
            XxlJobLogger.log("入库时间是：【" + nowTime + "】");
            XxlJobLogger.log("内容时间是：【" + rPressObject.getTextTime() + "】");
            XxlJobLogger.log("在规则《" + rPressObject.getRuletype() + "》的公式：【" + rPressObject.getRuleText() + "】");
            XxlJobLogger.log("符合分析计算结果为《" + rPressObject.getType() + "》");
            XxlJobLogger.log("内容摘要：【" + rPressObject.getAbstractText() + "】");
            XxlJobLogger.log("入库内容是：【" + rPressObject.getTextContent() + "】");
        }
        return inDb;
    }

    private int inDbOperation(RulePressObject rPressObject) {
        int inDb = 0;
        Storeresult storeresult = new Storeresult();
        storeresult.setVentcontent(rPressObject.getTextContent());
        storeresult.setDatasource(rPressObject.getTextFrom());
        storeresult.setTimeofoccurrence(rPressObject.getTextTime());
        storeresult.setEventclassification(rPressObject.getRuleName());
        storeresult.setAbstracttext(rPressObject.getAbstractText());
        storeresult.setRuletype(rPressObject.getRuletype());
        storeresult.setLocation("");
        String nowTime = TimeTools.getNowTime();
        storeresult.setIndbtime(nowTime);
        inDb = saveStoreresult(storeresult);
        //XxlJobLogger.log("入库时间是：【" + nowTime + "】");
        //XxlJobLogger.log("内容时间是：【" + rPressObject.getTextTime() + "】");
        //XxlJobLogger.log("在规则《" + rPressObject.getRuleName() + "》的公式：【" + rPressObject.getRuleText()+ "】风险级别：【"+rPressObject.getRuletype()+"】");
        //XxlJobLogger.log("符合分析计算结果为《" + rPressObject.getType() + "》");
        //XxlJobLogger.log("内容摘要：【" + rPressObject.getAbstractText() + "】");
        //XxlJobLogger.log("入库内容是：【" + rPressObject.getTextContent() + "】");
        return inDb;
    }

    /**
     * @param stringCase 待分析内容
     * @param typeName   分析后属于类别
     * @param typekey    将要插入自定义字典库的词
     */
    private void procceWordForCRF(String[] stringCase, String typeName, String[] typekey, String rulename) {
        Segment segment = new CRFSegment().enableCustomDictionary(true).enableOrganizationRecognize(true).enableNameRecognize(true);

        for (int t = 0; t < typekey.length; t++) {
            CustomDictionary.insert(typekey[t]);
        }
        //句子级别相似度添加关键词入字典分析库
        //提供之前处理
        List<Store> stores = new ArrayList<>(0);
        for (String sentence : stringCase) {
            List<Term> termList = segment.seg(GraphAiUtils.filterString(sentence));
            String trr = GraphAiUtils.filterString(sentence.toString().trim());
            //System.out.println(termList);
            for (int tt = 0; tt < typekey.length; tt++) {
                String key = GraphAiUtils.filterString(typekey[tt]);
                List<Term> seg = segment.seg(key);
                Term term = seg.get(0);
                String keynature = term.nature.toString();
                String keyword = term.word;
                for (int i = 0; i < termList.size(); i++) {
                    String word = termList.get(i).word;//词
                    String nature = termList.get(i).nature.toString();//词性
                    if (trr.indexOf(word) > 0) {
                        Store store = new Store();
                        store.setKeyWord(keyword);
                        store.setContent(sentence);
                        stores.add(store);
                        System.out.println(sentence);
                        XxlJobLogger.log("第二种方法根据【" + rulename + "】大类中的规则关键词【" + keyword + "】分析的内容是：{" + trr + "}");
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
        for (int q = 0; q < stores1.size(); q++) {
            Store store = stores1.get(q);
            String keyWord = store.getKeyWord();
            String content = store.getContent();
            ;
            XxlJobLogger.log("关键字【" + keyWord + "】处理后符合的段落是：【" + GraphAiUtils.filterString(content) + "】");
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
        XxlJobLogger.log("手工设置取出：【" + num + "】条符合关键词：【" + typekey + "】的事件有（ " + storelist.size() + " ）条！");
        List<String> returntext = new ArrayList<String>(0);
        int cont = 1;
        for (int tt = 0; tt < storelist.size(); tt++) {
            String s = storelist.get(tt);
            XxlJobLogger.log("根据【" + rulename + "】大类中的规则关键词【" + typekey + "】分析的第<" + cont + ">条内容是：{" + s + "}");
            returntext.add(s);
            cont++;
        }
        return returntext;
    }

    //组合词组
    public List<String> getZuHe(List<String> daizuhe01, List<String> daizuhe02) {
        List<String> returnlis = new ArrayList<>(0);
        Map<String, String> map = new HashMap<String, String>();
        for (int z1 = 0; z1 < daizuhe01.size(); z1++) {
            String s2 = daizuhe01.get(z1);
            //做两两组合
            for (int z11 = 0; z11 < daizuhe02.size(); z11++) {
                String concat = s2.concat(daizuhe02.get(z11));
                //returnlis.add(concat);
                //去重复处理
                map.put(concat, String.valueOf(z11));
            }
        }
        Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
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
        //XxlJobLogger.log("变更数据源！");
        DataSourceContextHolder.setDbType("ds1");     //注意这里在调用userMapper前切换到ds1的数据源
        List<Crawlercontent> crawlercontents = new ArrayList<Crawlercontent>(0);
        if (!StringUtils.isEmpty(param)) {
            if (TimeTools.isValidDate(param)) {
                crawlercontents = crawlercontentDao.findPageListByTime(param);
            } else if("init".equals(param)) {
                crawlercontents = crawlercontentDao.firstPageList();
            }else{
                crawlercontents = crawlercontentDao.pageList();
            }
        } else {
            crawlercontents = crawlercontentDao.pageList();
        }
        //XxlJobLogger.log("查询出待分析内容【" + crawlercontents.size() + "】");
        restoreDefaultDataSource();
        //XxlJobLogger.log("恢复默认数据源！");
        return crawlercontents;
    }

    //获取规则列表
    public List<Nlprule> findNlprule(String parm) {
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        String reg = "[\\u4e00-\\u9fa5]+";
        List<Nlprule> nlprules = new ArrayList<Nlprule>(0);
        if (!StringUtils.isEmpty(parm)) {
            if (parm.matches(reg)) {
                nlprules = nlpruleDao.pageListForRuleName(parm);
            } else {
                nlprules = nlpruleDao.pageList();
            }
        } else {
            nlprules = nlpruleDao.pageList();
        }
        restoreDefaultDataSource();
        //XxlJobLogger.log("恢复默认数据源！");
        return nlprules;
    }

    //数据来源ds2
    public void findByIdDs2() {
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds2的数据源

        List<Lawdreg> testInfos = lawdregDao.pageList();
        System.out.println("");
    }

    //数据俩ds2
    public int saveStoreresult(Storeresult storeresults) {
        DataSourceContextHolder.setDbType("ds2");     //注意这里在调用userMapper前切换到ds1的数据源
        storeresults.setId(UuidUtil.get32UUID());
        int save = storeresoultDao.save(storeresults);
        restoreDefaultDataSource();
        //XxlJobLogger.log("恢复默认数据源！");
        return save;
    }

    public void insertBatch() {
        long start = System.currentTimeMillis();
        List<Ienvironment> list = new ArrayList<>();
        Ienvironment ienvironment;
        for (int i = 0; i < 10000; i++) {
            ienvironment = new Ienvironment();
            ienvironment.setId(UuidUtil.get32UUID());
            ienvironment.setNlptype("type1" + i);
            ienvironment.setNlptime("102211" + i);
            ienvironment.setNlpcontent("ssdfssdfsdfsdf" + i);
            list.add(ienvironment);
        }
        try {
            XxlJobLogger.log("注切换数据源定ds2！");
            DataSourceContextHolder.setDbType("ds2");     //注意这里在调用前切换数据源
            ienvironmentDao.insertBatch(list);

        } catch (Exception e) {
            XxlJobLogger.log("批量插入数据错误！" + e.getMessage());
        }
        long end = System.currentTimeMillis();
        XxlJobLogger.log("批量插入数据用时【" + (start - end) + "】并恢复数据源！");
        restoreDefaultDataSource();//恢复数据源
    }

    public void restoreDefaultDataSource() {
        String dbType = DataSourceContextHolder.getDbType();
        if (!"ds0".equals(dbType)) {
            DataSourceContextHolder.setDbType("ds0");
        }
    }


    /**
     * 将规则与文本进行比对返回相应的规则公式结果和对应的文本。
     *
     * @param rule         //规则
     * @param texts        //文本
     * @param sentenceCont //摘要数量
     */
    public List<RulePressObject> ruleformles(List<Nlprule> rule, List<Crawlercontent> texts, int sentenceCont) {

        if (sentenceCont <= 0) {
            sentenceCont = 1;
        }
        //手动添加关键词
        for (Nlprule rul : rule) {
            //System.out.println(rul);
            String[] split = GraphAiUtils.filterString(rul.getRuleformula(), ",").split(",");
            for (String sp : split) {
                String s = GraphAiUtils.filterString(sp);
                if (!StringUtils.isEmpty(s)) {
                    CustomDictionary.add(s);
                    //System.out.println(s);
                }
            }
        }
        List<RulePressObject> ls = new ArrayList<>(0);
        RulePressObject rulePressObject = null;
        StringBuffer sb = new StringBuffer("");
        int rulestepsize = 1;
        int rulecountsize = rule.size();
        //入库数量
        int indbcount = 0;
        //待分析量
        int textsCount = texts.size();
        //for (Nlprule rul : rule) {//rule 规则循环开始
            //System.out.println(rul);
            HanLP.Config.ShowTermNature = false;
            //texts 内容分段处理
            for (Crawlercontent crawlercontent : texts) {
                //texts 内容分段处理
                String htmltext = crawlercontent.getHtmltext();
                if (!StringUtils.isEmpty(htmltext)) {
                    String url = crawlercontent.getCrawlerurl();
                    String ip = UrlPrecces.gotIp(url);
                    //XxlJobLogger.log("开始分析来源：【" + url + "】的网页内容！");
                    WebSitSplit webSitSplit = new WebSitSplit();
                    String websitsplit = webSitSplit.websitsplit(WebSitSplitEnum.OTHER);
                    String[] split = htmltext.trim().split(websitsplit);
                    //XxlJobLogger.log("开始网页内容开始断句处理！当前内容分段数量【" + split.length + "】");
                    for (String snhtmltext : split) {
                        //String substring = snhtmltext.substring(snhtmltext.indexOf("<p>") + 3).trim();
                        for (Nlprule rul : rule) { //规则rule循环开始
                            List<Term> seg = BasicTokenizer.SEGMENT.enableCustomDictionary(true).seg(rul.getRuleformula());
                            String pcrcontent1 = GraphAiUtils.pfilterString(GraphAiUtils.filterString(snhtmltext));
                            List<Term> tex1 = BasicTokenizer.SEGMENT.enableCustomDictionary(true).seg(pcrcontent1);
                            List<String> sentenceList = HanLP.extractSummary(pcrcontent1, sentenceCont);
                            //取出要比对的句子
                            // StringBuffer sbOrg = new StringBuffer("");
                            //使用之前清空操作
                            sb.setLength(0);
                            for (Term tex1s : tex1) {
                                sb.append(tex1s.word).append(",");
                                //sbOrg.append(tex1s.nature.equals("nt"));
                            }
                            //取出公式关键词在句子中比对
                            for (int sets = 0; sets < seg.size(); sets++) {
                                Term str = seg.get(sets);
                                String word = str.word;
                                String nature = str.nature.toString();
                                //手动增加词
                                int i = sb.toString().indexOf(word);
                                //if(word.in)
                                if (i >= 0) {
                                    seg.set(sets, new Term("true", Nature.nx));
                                    //(seg.get(sets).word).replaceAll(word,"true");
                                } else if (!"w".equals(nature.trim())) {
                                    seg.set(sets, new Term("false", Nature.nx));
                                }
                                // System.out.println("【"+word+"】出现次数！【"+i+"】");
                            }
                            rulePressObject = new RulePressObject();
                            String s = seg.toString().replaceAll(",", "").replaceAll("\\+", "||").replaceAll("\\*", "&&");
                            rulePressObject.setRuleText(rul.getRuleformula());
                            rulePressObject.setTextContent(pcrcontent1);
                            //开始计算公式结果
                            String rep = s.replaceAll("\\[", "").replaceAll("\\]", "");
                            String s1 = GraphAiUtils.runString(rep.trim());

                            Boolean isStore = Boolean.valueOf(s1);
                            /*如果匹配上了则打印并输出*/
                            if (isStore) {
                                rulePressObject.setType(s1);
                                rulePressObject.setTextTime(crawlercontent.getCrawlertime());
                                rulePressObject.setTextFrom(crawlercontent.getCrawlerurl());
                                rulePressObject.setRuletype(rul.getRuletype());
                                rulePressObject.setRuleName(rul.getRulename());
                                //将摘要存入对象
                                //使用之前清空操作用于新的内容
                                sb.setLength(0);
                                for (String sent : sentenceList) {
                                    sb.append(sent).append(";");
                                }
                                rulePressObject.setAbstractText(sb.toString());

                                /**处理分析结果*/
                                int i = inDbOperation(rulePressObject);
                                if (i > 0) {
                                    indbcount++;
                                }
                                ls.add(rulePressObject);
                                XxlJobLogger.log("当前规则入库：【" + indbcount + "】条！");
                                //当前语句满足一个规则公式就跳出规则循环
                                break;
                                //XxlJobLogger.log("----------------------------------------------------------------------");
                                //XxlJobLogger.log("公式类别：【" + rul.getRulename() + "】");
                                //XxlJobLogger.log("公式内容：【" + rul.getRuleformula() + "】");
                                // XxlJobLogger.log("分析内容：【"+pcrcontent1+"】");
                                //XxlJobLogger.log("内容时间：【"+crawlercontent.getCrawlertime()+"】");
                                //XxlJobLogger.log("符合公式内容摘要：【" + sb.toString() + "】");
                                //XxlJobLogger.log("内容出处：【"+crawlercontent.getCrawlerurl()+"】");
                                //XxlJobLogger.log("解析结果：【"+s.trim()+"】");
                                //XxlJobLogger.log("计算结果：【" + s1 + "】");
                                //XxlJobLogger.log("----------------------------------------------------------------------");
                                // System.out.println("----------------------------------------------------------------------");
                                //System.out.println("公式类别：【" + rul.getRulename() + "】");
                                //System.out.println("公式内容：【" + rul.getRuleformula() + "】");
                                //System.out.println("分析内容：【"+pcrcontent1+"】");
                                //System.out.println("内容时间：【"+crawlercontent.getCrawlertime()+"】");
                                //System.out.println("符合公式内容摘要：【" + sb.toString() + "】");
                                //System.out.println("内容出处：【"+crawlercontent.getCrawlerurl()+"】");
                                //System.out.println("解析结果：【"+s.trim()+"】");
                                //System.out.println("计算结果：【" + s1 + "】");
                                //System.out.println("----------------------------------------------------------------------");
                            }
                        }//规则循环结束
                    }
                    //texts 处理结束
                    //XxlJobLogger.log("当前符合条数为：【" + ls.size() + "】条！");
                }
                XxlJobLogger.log("剩余待分析条数：【" + (textsCount-1) + "】");
            }
           // rulestepsize++;
        //}//rule 循环结束
        return ls;
    }
}

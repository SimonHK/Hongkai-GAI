package com.graphai.util;

import com.graphai.model.AnalysisResult;
import com.graphai.model.Locations;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.BasicTokenizer;
import com.mysql.jdbc.StringUtils;
import redis.clients.jedis.Jedis;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class GraphAiUtils {

    private static Jedis jedis;

    static {
        //连接服务器
        jedis = new Jedis("127.0.0.1", 6379);
        //权限认证
//      jedis.auth("");

    }

    /**
     * 将规则与文本进行比对返回相应的规则公式结果和对应的文本。
     *
     * @param rule       //需要匹配的关键字
     * @param ruleNature //关键字词性（自定义这些词性）
     * @param texts      //文本
     */
    public static List<AnalysisResult> ruleformles(List<Locations> rule, String ruleNature, String texts) {

        List<AnalysisResult> analysisResults = new ArrayList<>(0);

        for (Locations locations : rule) {
            String locationname = locations.getLocationname();
            String locationnameas = locations.getLocationalias();
            String[] split = locationnameas.split(",");
            if (ruleNature.equals(Nature.nt.toString().trim())) {
                CustomDictionary.insert(locationname, "nt 1024");
                for(String loceas : split){
                    if(!StringUtils.isNullOrEmpty(loceas)){
                        CustomDictionary.insert(loceas, "nt 1024");
                    }
                }

            }
            if (ruleNature.equals(Nature.nr.toString().trim())) {
                CustomDictionary.insert(locationname, "nr 1024");
            }
            if (ruleNature.equals(Nature.ns.toString().trim())) {
                CustomDictionary.insert(locationname, "ns 1024");
            }
        }

        List<Term> seg = BasicTokenizer
                .SEGMENT
                .enableCustomDictionary(true)
                .enableOrganizationRecognize(true)
                .enableNameRecognize(true)
                .enablePlaceRecognize(true)
                .enableAllNamedEntityRecognize(true)
                .enableNumberQuantifierRecognize(true)
                .enableTranslatedNameRecognize(true)
                .seg(texts);
        //System.out.println(seg);
        //断句
       /* List<List<Term>> lists = segment.seg2sentence(texts);
        System.out.println(lists);*/
        //取出要比对的句子
        StringBuffer sbnr = new StringBuffer("");
        StringBuffer sblocal = new StringBuffer("");
        StringBuffer sbTime = new StringBuffer("");
        StringBuffer orgLocsid = new StringBuffer("");
        for (Term tes : seg) {
            String natu = tes.nature.toString().trim();
            String word = tes.word.toString().trim();
            if (natu.equals(Nature.nr.toString().trim())) {
                sbnr.append(word).append(",");
            }
            if (natu.equals(Nature.nt.toString().trim())) {

                for(Locations locations:rule){
                    String locationid = locations.getLocationid();
                    String locationname = locations.getLocationname();
                    String locationalias = locations.getLocationalias();
                    String[] split = locationalias.split(",");
                    int i = 0;
                    for(String sp:split){
                        if(sp.equals(word) || word.contains(sp)){
                            i++;
                        }
                    }
                    if(locationname.equals(word) || i > 0 ){
                        orgLocsid.append(locationid).append(",");
                        sblocal.append(locationname).append(",");
                    }
                }
            }
            /*if(natu.equals(Nature.mq.toString().trim())){
                sbTime.append(word).append(",");
            }*/



        }
        String persionnames = GraphStringUtils.formatRepetition(sbnr.toString());
        sbnr.setLength(0);

        String orglocal = GraphStringUtils.formatRepetition(sblocal.toString());
        sblocal.setLength(0);

        String timesb = GraphStringUtils.formatRepetition(sbTime.toString());
        sbTime.setLength(0);

        String orglocalIds = GraphStringUtils.formatRepetition(orgLocsid.toString());
        orgLocsid.setLength(0);



        // StringBuffer sbOrg = new StringBuffer("");
        if(!StringUtils.isNullOrEmpty(orglocalIds)) {
            AnalysisResult analysisResult = new AnalysisResult();
            analysisResult.setAnalysisWord(orglocal);
            analysisResult.setAnalysisSentence(texts);
            analysisResult.setSentencePersonName(persionnames);
            analysisResult.setSentenceTimes(timesb);
            analysisResult.setLocationids(orglocalIds);
            analysisResult.setAnalysisNature(Nature.nt.toString().trim());
            analysisResults.add(analysisResult);

            /*for (Term tex1s : seg) {
                String natu = tex1s.nature.toString().trim();
                String word = tex1s.word.toString().trim();
                AnalysisResult analysisResult = new AnalysisResult();
                analysisResult.setAnalysisWord(orglocal);
                analysisResult.setAnalysisSentence(texts);
                analysisResult.setSentencePersonName(persionnames);
                analysisResult.setSentenceTimes(timesb);
                analysisResult.setLocationids(orglocalIds);
                for (Locations locations : rule) {
                    String locationname = locations.getLocationname();
                    String locationalias = locations.getLocationalias();
                    if (word.equals(locationname) || isIndexOf(locationalias, word) && natu.equals(Nature.nt.toString().trim())) {
                        analysisResult.setAnalysisNature(Nature.nt.toString().trim());
                        analysisResults.add(analysisResult);
                    } else if (word.equals(locationname) || isIndexOf(locationalias, word) && natu.equals(Nature.nr.toString().trim())) {
                        analysisResult.setAnalysisNature(Nature.nr.toString().trim());
                        analysisResults.add(analysisResult);
                    } else if (word.equals(locationname) || isIndexOf(locationalias, word) && natu.equals(Nature.ns.toString().trim())) {
                        analysisResult.setAnalysisNature(Nature.ns.toString().trim());
                        analysisResults.add(analysisResult);
                    }
                }
            }*/
        }
        //取出公式关键词在句子中比对
        /*for (int sets = 0; sets < seg.size(); sets++) {
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
        }*/

                /*XxlJobLogger.log("----------------------------------------------------------------------");
                XxlJobLogger.log("公式类别：【"+rul.getRulename()+"】");
                XxlJobLogger.log("公式内容：【"+rul.getRuleformula()+"】");
                XxlJobLogger.log("分析内容：【"+pcrcontent1+"】");
                XxlJobLogger.log("内容时间：【"+crawlercontent.getCrawlertime()+"】");
                XxlJobLogger.log("内容摘要：【"+absb.toString()+"】");
                XxlJobLogger.log("内容出处：【"+crawlercontent.getCrawlerurl()+"】");
                XxlJobLogger.log("解析结果：【"+s.trim()+"】");
                XxlJobLogger.log("计算结果：【"+s1+"】");
                XxlJobLogger.log("----------------------------------------------------------------------");*/
                /*System.out.println("----------------------------------------------------------------------");
                System.out.println("公式类别：【"+rul.getRulename()+"】");
                System.out.println("公式内容：【"+rul.getRuleformula()+"】");
                System.out.println("分析内容：【"+pcrcontent1+"】");
                System.out.println("内容时间：【"+crawlercontent.getCrawlertime()+"】");
                System.out.println("内容摘要：【"+absb.toString()+"】");
                System.out.println("内容出处：【"+crawlercontent.getCrawlerurl()+"】");
                System.out.println("解析结果：【"+s.trim()+"】");
                System.out.println("计算结果：【"+s1+"】");
                System.out.println("----------------------------------------------------------------------");*/

        return analysisResults;
    }

    /**
     * 将规则与文本进行比对返回相应的规则公式结果和对应的文本。
     *
     * @param ruleNature //关键字词性（自定义这些词性）
     * @param texts      //文本
     */
    public static List<AnalysisResult> ruleformles(String ruleNature, String texts) {

        List<AnalysisResult> analysisResults = new ArrayList<>(0);

        /*long startTime = System.currentTimeMillis();
        Long dbSize = getDbSize();
        System.out.println("开始加入机构词时间：" + startTime);
        for (long i = 1; i < dbSize; i++) {
            List<String> hvals = jedis.hvals("id:" + String.valueOf(i));
            //System.out.print("[0]"+hvals.get(0)+"[1]"+hvals.get(0)+"[2]"+hvals.get(0));
            Locations locations = new Locations();
            List<Locations> locationsList = new ArrayList<>(0);
            locations.setLocationid(hvals.get(2));
            locations.setLocationalias(hvals.get(1));
            locations.setLocationname(hvals.get(0));
            locationsList.add(locations);
            String locationname = locations.getLocationname();
            String locationnameas = locations.getLocationalias();
            String[] split = locationnameas.split(",");
            if (ruleNature.equals(Nature.nt.toString().trim())) {
                CustomDictionary.insert(locationname, "nt 1024");
                for(String loceas : split){
                    if(!StringUtils.isNullOrEmpty(loceas)){
                        CustomDictionary.insert(loceas, "nt 1024");
                    }
                }

            }
            if (ruleNature.equals(Nature.nr.toString().trim())) {
                CustomDictionary.insert(locationname, "nr 1024");
            }
            if (ruleNature.equals(Nature.ns.toString().trim())) {
                CustomDictionary.insert(locationname, "ns 1024");
            }
        }
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.print("结束加载机构时间：" + endTime);
        System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
*/

        List<Term> seg = BasicTokenizer
                .SEGMENT
                .enableCustomDictionary(true)
                .enableOrganizationRecognize(true)
                .enableNameRecognize(true)
                .enablePlaceRecognize(true)
                .enableAllNamedEntityRecognize(true)
                .enableNumberQuantifierRecognize(true)
                .enableTranslatedNameRecognize(true)
                .seg(texts);
        //System.out.println(seg);
        //断句
       /* List<List<Term>> lists = segment.seg2sentence(texts);
        System.out.println(lists);*/
        //取出要比对的句子
        StringBuffer sbnr = new StringBuffer("");
        StringBuffer sblocal = new StringBuffer("");
        StringBuffer sbTime = new StringBuffer("");
        StringBuffer orgLocsid = new StringBuffer("");

        for (Term tes : seg) {
            String natu = tes.nature.toString().trim();
            String word = tes.word.toString().trim();
            if (natu.equals(Nature.nr.toString().trim())) {
                sbnr.append(word).append(",");
            }
            if (natu.equals(Nature.nt.toString().trim())) {

                long startTime = System.currentTimeMillis();
                Long dbSize = getDbSize();
                System.out.println("开始遍历机构信息：" + startTime);
                for (long ii = 1; ii < dbSize; ii++) {
                    List<String> hvals = jedis.hvals("id:" + String.valueOf(ii));
                    //System.out.print("[0]"+hvals.get(0)+"[1]"+hvals.get(0)+"[2]"+hvals.get(0));
                    Locations locations = new Locations();
                    List<Locations> locationsList = new ArrayList<>(0);
                    locations.setLocationid(hvals.get(2));
                    locations.setLocationalias(hvals.get(1));
                    locations.setLocationname(hvals.get(0));
                    locationsList.add(locations);
                    String locationid = locations.getLocationid();
                    String locationname = locations.getLocationname();
                    String locationalias = locations.getLocationalias();
                    String[] split = locationalias.split(",");
                    int i = 0;
                    System.out.println("分析词比对：【"+word+"】与机构【" + locationname+"】及别名【"+locationalias+"】");
                    for(String sp:split){
                        if(sp.equals(word) || word.contains(sp)){
                            i++;
                        }
                    }
                    if(locationname.equals(word) || i > 0 ){
                        orgLocsid.append(locationid).append(",");
                        sblocal.append(locationname).append(",");
                        System.out.println("满足结果：【"+word+"】与机构【" + locationname+"】及别名【"+locationalias+"】");
                    }
                }
                long endTime = System.currentTimeMillis(); //获取结束时间
                System.out.print("结束结束遍历时间：" + endTime);
                System.out.println("遍历处理运行时间： " + (endTime - startTime) + "ms");
            }
        }
        String persionnames = GraphStringUtils.formatRepetition(sbnr.toString());
        sbnr.setLength(0);

        String orglocal = GraphStringUtils.formatRepetition(sblocal.toString());
        sblocal.setLength(0);

        String timesb = GraphStringUtils.formatRepetition(sbTime.toString());
        sbTime.setLength(0);

        String orglocalIds = GraphStringUtils.formatRepetition(orgLocsid.toString());
        orgLocsid.setLength(0);

        if(!StringUtils.isNullOrEmpty(orglocalIds)) {
            AnalysisResult analysisResult = new AnalysisResult();
            analysisResult.setAnalysisWord(orglocal);
            analysisResult.setAnalysisSentence(texts);
            analysisResult.setSentencePersonName(persionnames);
            analysisResult.setSentenceTimes(timesb);
            analysisResult.setLocationids(orglocalIds);
            analysisResult.setAnalysisNature(Nature.nt.toString().trim());
            analysisResults.add(analysisResult);
        }

        return analysisResults;
    }


        public static Long getDbSize() {
            return jedis.dbSize();
        }



        private static boolean isIndexOf(String source, String word){
        boolean index = false;
        String s = null;
        String s1 = null;
        try {
            s = new String(source.getBytes(), "utf-8");
            s1 = new String(word.getBytes(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(s.indexOf(s1) != -1){
            index = true;
        }
        return index;

    }

    public static void main(String[] args) {
        String testCase = "2018年4月12日我在上海林原科技有限公司兼职工作，新浪美股路透中文网讯，于泓楷经常带着马云在台川喜宴餐厅吃饭，偶尔去开元地中海影城看电影。通中非基金黄光裕在国家性质的投资机构共事，其坐落在北京市复兴门外大街。";

        List<Locations> locations = new ArrayList<>(0);

        Locations locations1 = new Locations();
        locations1.setLocationalias("复兴门外大街");
        locations1.setLocationid("12132131");
        locations1.setLocationname("北京市复兴门外大街");
        locations.add(locations1);
        Locations locations2 = new Locations();
        locations2.setLocationalias("中华产业园");
        locations2.setLocationid("12132132");
        locations2.setLocationname("产业园");
        locations.add(locations2);

        //List<AnalysisResult> ruleformles = ruleformles(locations, "ns", testCase);
        /*for (AnalysisResult analysisResult : ruleformles) {
            System.out.println("["+analysisResult.getSentenceTimes()+"][" + analysisResult.getAnalysisWord() + "][" + analysisResult.getAnalysisNature() + "][" + analysisResult.getSentencePersonName() + "][" + analysisResult.getAnalysisSentence() + "]");
        }*/
    }

}

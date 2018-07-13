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
import com.xxl.job.core.log.XxlJobLogger;
import redis.clients.jedis.Jedis;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                for (String loceas : split) {
                    if (!StringUtils.isNullOrEmpty(loceas)) {
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

                for (Locations locations : rule) {
                    String locationid = locations.getLocationid();
                    String locationname = locations.getLocationname();
                    String locationalias = locations.getLocationalias();
                    String[] split = locationalias.split(",");
                    int i = 0;
                    for (String sp : split) {
                        if (sp.equals(word) || word.contains(sp)) {
                            i++;
                        }
                    }
                    if (locationname.equals(word) || i > 0) {
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
        if (!StringUtils.isNullOrEmpty(orglocalIds)) {
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
        //StringBuffer sbTime = new StringBuffer("");
        StringBuffer orgLocsid = new StringBuffer("");
        long startTime = System.currentTimeMillis();
        XxlJobLogger.log("处理当前事件运行开始时间[" + startTime + "]");
        //分析语料中含有人名的内容
        for (Term tenr : seg) {
            String natunr = tenr.nature.toString().trim();
            String wordnr = tenr.word.toString().trim();
            if (natunr.equals(Nature.nr.toString().trim())) {
                sbnr.append(wordnr).append(",");
            }
        }
        String persionnames = GraphStringUtils.formatRepetition(sbnr.toString());
        sbnr.setLength(0);
        //分析语料中含有机构及实体地域的内容
        AnalysisResult analysisResult = null;
        for (Term tes : seg) {
            String natu = tes.nature.toString().trim();
            String word = tes.word.toString().trim();
            if (natu.equals(Nature.nt.toString().trim())) {
                String ssss = MD5.stringToMsqlMD5(word);
                // System.out.println("中文转MD="+ssss);
                Map<String, String> stringStringMap = jedis.hgetAll(ssss);
                String locationid = stringStringMap.get("locationid");
                //System.out.println("============="+locationid);
                if (!StringUtils.isNullOrEmpty(locationid)) {
                    analysisResult = new AnalysisResult();
                    analysisResult.setAnalysisWord(word);
                    analysisResult.setAnalysisSentence(texts);
                    analysisResult.setSentencePersonName(persionnames);
                    //analysisResult.setSentenceTimes(timesb);
                    analysisResult.setLocationids(locationid);
                    analysisResult.setAnalysisNature(Nature.nt.toString().trim());
                    analysisResults.add(analysisResult);

                    //System.out.println("满足结果：【"+word+"】与机构【" + locationname+"】及别名【"+locationalias+"】");
                    XxlJobLogger.log("满足location结果：【" + word + "】与locationID【" + locationid + "】");
                }
                //}

                // System.out.print("结束结束遍历时间：" + endTime);
                //System.out.println("遍历处理运行时间： " + (endTime - startTime) + "ms");
            }
        }
        long endTime = System.currentTimeMillis(); //获取结束时间
        XxlJobLogger.log("处理当前事件运行结束时间： " + (endTime - startTime) + "ms");

        return analysisResults;
    }


    public static Long getDbSize() {
        return jedis.dbSize();
    }


    private static boolean isIndexOf(String source, String word) {
        boolean index = false;
        String s = null;
        String s1 = null;
        try {
            s = new String(source.getBytes(), "utf-8");
            s1 = new String(word.getBytes(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (s.indexOf(s1) != -1) {
            index = true;
        }
        return index;

    }


}

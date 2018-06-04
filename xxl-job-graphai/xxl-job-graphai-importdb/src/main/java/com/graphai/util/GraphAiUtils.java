package com.graphai.util;

import com.graphai.model.AnalysisResult;
import com.graphai.model.Locations;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;


import java.util.ArrayList;
import java.util.List;

public class GraphAiUtils {

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
            if (ruleNature.equals(Nature.nt.toString().trim())) {
                CustomDictionary.insert(locationname, "nt 1024");
            }

            if (ruleNature.equals(Nature.nr.toString().trim())) {
                CustomDictionary.insert(locationname, "nr 1024");
            }

            if (ruleNature.equals(Nature.ns.toString().trim())) {
                CustomDictionary.insert(locationname, "ns 1024");
            }
        }
        // List<String> ls  = new ArrayList<>(0);
        // List<Term> seg = BasicTokenizer.SEGMENT.enableOrganizationRecognize(true).enableCustomDictionary(true).seg(texts);
        Segment segment = HanLP.newSegment()
                .enableOrganizationRecognize(true)
                .enableNameRecognize(true)
                .enablePlaceRecognize(true)
                .enableCustomDictionary(true)
                .enableAllNamedEntityRecognize(true)
                //.enableIndexMode(true)
                //.enableMultithreading(true)
                .enableNumberQuantifierRecognize(true)
                //.enableOffset(true)
                //.enablePartOfSpeechTagging(true)
                .enableTranslatedNameRecognize(true)
                ;
        List<Term> seg = segment.seg(texts);
        //System.out.println(seg);
        //断句
       /* List<List<Term>> lists = segment.seg2sentence(texts);
        System.out.println(lists);*/
        //取出要比对的句子
        StringBuffer sb = new StringBuffer("");
        StringBuffer sblocal = new StringBuffer("");
        StringBuffer sbTime = new StringBuffer("");
        StringBuffer orgLocsid = new StringBuffer("");
        for (Term tes : seg) {
            String natu = tes.nature.toString().trim();
            String word = tes.word.toString().trim();
            if (natu.equals(Nature.nr.toString().trim())) {
                sb.append(word).append(",");
            }
            if (natu.equals(Nature.nt.toString().trim())) {
                sblocal.append(word).append(",");
                for(Locations locations:rule){
                    String locationid = locations.getLocationid();
                    String locationname = locations.getLocationname();
                    if(word.equals(locationname)){
                        orgLocsid.append(locationid).append(",");
                    }
                }
            }
            if(natu.equals(Nature.mq.toString().trim())){
                sbTime.append(word).append(",");
            }
        }
        String persionnames = sb.toString();
        String orglocal = sblocal.toString();
        String timesb = sbTime.toString();
        String orglocalIds = orgLocsid.toString();
        // StringBuffer sbOrg = new StringBuffer("");
        for (Term tex1s : seg) {
            String natu = tex1s.nature.toString().trim();
            String word = tex1s.word.toString().trim();
            AnalysisResult analysisResult = new AnalysisResult();
            analysisResult.setAnalysisWord(orglocal);
            analysisResult.setAnalysisSentence(texts);
            analysisResult.setSentencePersonName(persionnames);
            analysisResult.setSentenceTimes(timesb);
            analysisResult.setLocationids(orglocalIds);
            for(Locations locations:rule) {
                String locationname = locations.getLocationname();
                if (word.equals(locationname) && natu.equals(Nature.nt.toString().trim())) {
                    //符合机构
                    analysisResult.setAnalysisNature(Nature.nt.toString().trim());
                    analysisResults.add(analysisResult);
                } else if (word.equals(locationname) && natu.equals(Nature.nr.toString().trim())) {
                    //符合人名
                    analysisResult.setAnalysisNature(Nature.nr.toString().trim());
                    analysisResults.add(analysisResult);
                } else if (word.equals(locationname) && natu.equals(Nature.ns.toString().trim())) {
                    //符合地名
                    analysisResult.setAnalysisNature(Nature.ns.toString().trim());
                    analysisResults.add(analysisResult);
                }
            }

            //sbOrg.append(tex1s.nature.equals("nt"));
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

        List<AnalysisResult> ruleformles = ruleformles(locations, "ns", testCase);
        for (AnalysisResult analysisResult : ruleformles) {
            System.out.println("["+analysisResult.getSentenceTimes()+"][" + analysisResult.getAnalysisWord() + "][" + analysisResult.getAnalysisNature() + "][" + analysisResult.getSentencePersonName() + "][" + analysisResult.getAnalysisSentence() + "]");
        }
    }

}

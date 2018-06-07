package com.graphai.util;

import com.alibaba.druid.util.StringUtils;
import com.graphai.model.Crawlercontent;
import com.graphai.model.Nlprule;
import com.graphai.model.RulePressObject;
import com.graphai.model.Store;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.BasicTokenizer;
import com.xxl.job.core.log.XxlJobLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class GraphAiUtils {

    static ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");

    /**
    * 字符串过滤特殊符号处理
    * */
    public static String filterString(String str){
        return str.trim().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+", "");
    }
    /**
     * 处理特殊字符非标点符号。
     * */
    public static String pfilterString(String str){
        return str.trim().replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】\\s+\\s*\\t]+", "");
    }

    public static String filterString(String str,String str1){
        return str.trim().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+", str1);
    }

    public static List<Store> ifRepeat(List<Store> arr){
        List<Store> ls = new ArrayList<>(0);
        LinkedHashSet<Store> haoma = new LinkedHashSet<Store>();
        for (int i = 0; i < arr.size(); i++) {
            haoma.add(arr.get(i));
        }
        // 创建迭代器
        Iterator<Store> iterator = haoma.iterator();
        int a = 0;
        // 迭代集合
        while (iterator.hasNext()) { // true
           // Object c = iterator.next();
            ls.add(iterator.next());

            //System.out.println(c);
        }

        return ls;
    }

    /**
     * 计算公式结果
     * */
    public static String runString(String string){
        try {
            return jse.eval(string).toString();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return string;
    }


    /**
     * 将规则与文本进行比对返回相应的规则公式结果和对应的文本。
     * @param rule //规则
     * @param texts //文本
     * @param sentenceCont //摘要数量
     * */
    public static List<RulePressObject> ruleformles(List<Nlprule> rule, List<Crawlercontent> texts,int sentenceCont) {

        if (sentenceCont <= 0 ){
            sentenceCont = 1;
        }
        //手动添加关键词
        for(Nlprule rul:rule){
            //System.out.println(rul);
            String[] split = GraphAiUtils.filterString(rul.getRuleformula(), ",").split(",");
            for(String sp:split){
                String s = GraphAiUtils.filterString(sp);
                if(!StringUtils.isEmpty(s)){
                    CustomDictionary.add(s);
                    //System.out.println(s);
                }
            }
        }
        List<RulePressObject> ls = new ArrayList<>(0);
        RulePressObject rulePressObject = null;
        StringBuffer sb  = new StringBuffer("");
        for(Nlprule rul:rule){
            //System.out.println(rul);
            HanLP.Config.ShowTermNature = false;
            for(Crawlercontent crawlercontent:texts){
                List<Term> seg = BasicTokenizer.SEGMENT.enableCustomDictionary(true).seg(rul.getRuleformula());
                String pcrcontent1 = filterString(crawlercontent.getCrawlercontent()).trim();
                List<Term> tex1 = BasicTokenizer.SEGMENT.enableCustomDictionary(true).seg(pcrcontent1);
                List<String> sentenceList = HanLP.extractSummary(pcrcontent1, sentenceCont);
                //取出要比对的句子
                // StringBuffer sbOrg = new StringBuffer("");
                //使用之前清空操作
                sb.setLength(0);
                for(Term tex1s:tex1){
                    sb.append(tex1s.word).append(",");
                    //sbOrg.append(tex1s.nature.equals("nt"));
                }
                //取出公式关键词在句子中比对
                for(int sets = 0; sets < seg.size();sets++){
                    Term str = seg.get(sets);
                    String word = str.word;String nature = str.nature.toString();
                    //手动增加词
                    int i = sb.toString().indexOf(word);
                    //if(word.in)
                    if(i >= 0){
                        seg.set(sets,new Term("true", Nature.nx));
                        //(seg.get(sets).word).replaceAll(word,"true");
                    }else if(!"w".equals(nature.trim())){
                        seg.set(sets,new Term("false", Nature.nx));
                    }
                    // System.out.println("【"+word+"】出现次数！【"+i+"】");
                }
                rulePressObject = new RulePressObject();
                String s = seg.toString().replaceAll(",", "").replaceAll("\\+","||").replaceAll("\\*","&&");
                rulePressObject.setRuleText(rul.getRuleformula());
                rulePressObject.setTextContent(pcrcontent1);
                //开始计算公式结果
                String rep = s.replaceAll("\\[", "").replaceAll("\\]", "");
                String s1 = GraphAiUtils.runString(rep.trim());

                Boolean isStore = Boolean.valueOf(s1);
                /*如果匹配上了则打印并输出*/
                if(isStore) {
                    rulePressObject.setType(s1);
                    rulePressObject.setTextTime(crawlercontent.getCrawlertime());
                    rulePressObject.setTextFrom(crawlercontent.getCrawlerurl());
                    rulePressObject.setRuleType(rul.getRulename());
                    //将摘要存入对象
                    //使用之前清空操作用于新的内容
                    sb.setLength(0);
                    for (String sent : sentenceList) {
                        sb.append(sent).append(";");
                    }
                    rulePressObject.setAbstractText(sb.toString());
                    ls.add(rulePressObject);
                    //XxlJobLogger.log("----------------------------------------------------------------------");
                    XxlJobLogger.log("公式类别：【" + rul.getRulename() + "】");
                    //XxlJobLogger.log("公式内容：【" + rul.getRuleformula() + "】");
                    // XxlJobLogger.log("分析内容：【"+pcrcontent1+"】");
                    //XxlJobLogger.log("内容时间：【"+crawlercontent.getCrawlertime()+"】");
                    XxlJobLogger.log("符合公式内容摘要：【" + sb.toString() + "】");
                    //XxlJobLogger.log("内容出处：【"+crawlercontent.getCrawlerurl()+"】");
                    //XxlJobLogger.log("解析结果：【"+s.trim()+"】");
                    //XxlJobLogger.log("计算结果：【" + s1 + "】");
                    XxlJobLogger.log("----------------------------------------------------------------------");
                   // System.out.println("----------------------------------------------------------------------");
                    System.out.println("公式类别：【" + rul.getRulename() + "】");
                    //System.out.println("公式内容：【" + rul.getRuleformula() + "】");
                    //System.out.println("分析内容：【"+pcrcontent1+"】");
                    //System.out.println("内容时间：【"+crawlercontent.getCrawlertime()+"】");
                    System.out.println("符合公式内容摘要：【" + sb.toString() + "】");
                    //System.out.println("内容出处：【"+crawlercontent.getCrawlerurl()+"】");
                    //System.out.println("解析结果：【"+s.trim()+"】");
                    //System.out.println("计算结果：【" + s1 + "】");
                    System.out.println("----------------------------------------------------------------------");
                }
            }
        }

        return ls;
    }


    /**
     * 将规则与文本进行比对返回相应的规则公式结果和对应的文本。
     *
     * @param rule  //需要匹配的关键字
     * @param texts //文本
     */
    public static List<String> ruleformles(String rule, String texts) {

        String[] split1 = rule.split(",");
        if (split1.length > 0) {
            for (String sp : split1) {
                CustomDictionary.add(sp,"nt");
            }
        } else {
            CustomDictionary.add(rule,"nt");
        }

        List<String> ls  = new ArrayList<>(0);
        List<Term> tex1 =  BasicTokenizer.SEGMENT.enableOrganizationRecognize(true).enableCustomDictionary(true).seg(texts);
        //取出要比对的句子
        StringBuffer sb = new StringBuffer("");
        // StringBuffer sbOrg = new StringBuffer("");
        for (Term tex1s : tex1) {
            sb.append(tex1s.word).append(",");
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

        return ls;
    }

    public static void main(String[] args){
        String testCase = "我在上海林原科技有限公司兼职工作，我经常在台川喜宴餐厅吃饭，偶尔去开元地中海影城看电影。";

        List<String> ruleformles = ruleformles("上海林原科技有限公司,林原科技",testCase);
    }

}

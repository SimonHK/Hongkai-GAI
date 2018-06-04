package com.hankcs;

import com.alibaba.druid.util.StringUtils;
import com.graphai.model.RulePressObject;
import com.graphai.util.GraphAiUtils;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.RunString;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.BasicTokenizer;
import com.sun.tools.javac.code.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestRule {



    public static void main(String[] args) {
        String[] rule = new String[]{
                "((业+行业+产业)*(出现+显露+陷入)*衰退)*(你好+我们)",
                "(水灾+地震+龙卷风+洪灾+泥石流+滑坡+海啸+冰雹+飓风+台风+禽流感+疫)*(财产损失严重+物资损失严重)",
                "(地区+区+国+区域+地域+省+市+县)*(发生+爆发+出现+引发)*(水灾+地震+龙卷风+洪灾+泥石流+滑坡+海啸+冰雹+飓风+台风+禽流感+疫)",
        };
        //将段落与公式中的关键字进行匹配
        String[] ter  = {"绩效产能产业进度要好不知道可不可以！衰退在我们的国家队，显露中国足球走出国门就疲软！","显露的内容总是不弱衰退的内容多！"};
        List<RulePressObject> ruleformle;//ruleformle(rule,ter);
        ruleformle = ruleformles(rule, ter);
        for(int i  = 0 ; i < ruleformle.size(); i++){
            RulePressObject rulePressObject = ruleformle.get(i);
            String ruleText = rulePressObject.getRuleText();
            String textContent = rulePressObject.getTextContent();
            //System.out.println(ruleText+"<>"+textContent);
        }

    }

    private static List<String> ruleformle(String[] rule,String[] ter) {
        ArrayList<String> strings = new ArrayList<>(0);
        int y = 0;
        boolean b = false;
        for (int k = 0; k < rule.length; k++) {
            String s = rule[k];
            String[] split = ter;
            for (int o = 0; o < split.length; o++) {
                String s1 = split[o].toString();
                if (!StringUtils.isEmpty(s1)) {
                    if(b = s.indexOf(s1) > 0) {
                        s = s.replaceAll("\\b" + s1, String.valueOf(b));
                    }
                }
            }
            String all = s.replaceAll("\\[", "(").replaceAll("\\+", "||").replaceAll("\\*", "&&").replaceAll("\\]", ")");
            strings.add(all);
            System.out.println(all);
        }
        return strings;
    }

    protected static int  getChinaNum(String str) {
        int amount = 0;// 创建汉字数量计数器
        //String exp="^[\u4E00-\u9FA5|\\！|\\+|\\,|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]$";
        //String exp="^[\u4E00-\u9FA5|\\！|\\*|\\+|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]$";
        String exp="^[\u4E00-\u9FA5|\u4E00-\u9FA5|\\+-|\\+]$";

        Pattern pattern=Pattern.compile(exp);
        for (int i = 0; i < str.length(); i++) {// 遍历字符串每一个字符
            char c = str.charAt(i);
            Matcher matcher=pattern.matcher(c + "");
            if(matcher.matches()) {
                amount++;
                System.out.println("["+c+"]位置["+i+"]");
            }
        }
        return amount;
    }

    /**
     * 将规则与文本进行比对返回相应的规则公式结果和对应的文本。
     * @param rule //规则
     * @param texts //文本
     * */
    private static List<RulePressObject> ruleformles(String[] rule, String[] texts) {

        //手动添加关键词
        for(String rul:rule){
         //System.out.println(rul);
            String[] split = GraphAiUtils.filterString(rul, ",").split(",");
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
        for(String rul:rule){
            //System.out.println(rul);
            HanLP.Config.ShowTermNature = false;
            for(String text:texts){
                List<Term> seg = BasicTokenizer.SEGMENT.enableCustomDictionary(true).seg(rul);
                List<Term> tex1 = BasicTokenizer.SEGMENT.enableCustomDictionary(true).seg(text);
                //取出要比对的句子
                StringBuffer sb  = new StringBuffer("");
                for(Term tex1s:tex1){
                    sb.append(tex1s.word).append(",");
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
                rulePressObject.setRuleText(s);
                rulePressObject.setTextContent(text);
                //开始计算公式结果
                String rep = s.replaceAll("\\[", "").replaceAll("\\]", "");
                String s1 = GraphAiUtils.runString(rep.trim());
                rulePressObject.setType(s1);
                ls.add(rulePressObject);
                System.out.println("----------------------------------------------------------------------");
                System.out.println("公式内容：【"+rul+"】");
                System.out.println("分析内容：【"+text+"】");
                System.out.println("解析结果：【"+s.trim()+"】");
                System.out.println("计算结果：【"+s1+"】");
                System.out.println("----------------------------------------------------------------------");
            }
        }

        return ls;
    }

}

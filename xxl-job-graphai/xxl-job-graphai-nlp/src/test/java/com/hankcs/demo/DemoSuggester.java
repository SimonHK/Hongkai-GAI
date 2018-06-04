/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/9 13:27</create-date>
 *
 * <copyright file="DemoSuggestor.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.demo;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.suggest.Suggester;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文本推荐(句子级别，从一系列句子中挑出与输入句子最相似的那一个)
 * @author hankcs
 */
public class DemoSuggester
{
    public static void main(String[] args) throws IOException {
        testFinder();

    }

    public static void testFinder() throws IOException {
        Suggester suggester = new Suggester();
        /*String[] titleArray =
        (
                "威廉王子发表演说 呼吁保护野生动物\n" +
                "魅惑天后许佳慧不爱“预谋” 独唱《许某某》\n" +
                "《时代》年度人物最终入围名单出炉 普京马云入选\n" +
                "“黑格比”横扫菲：菲吸取“海燕”经验及早疏散\n" +
                "日本保密法将正式生效 日媒指其损害国民知情权\n" +
                "英报告说空气污染带来“公共健康危机”"
        ).split("\\n");*/

        FileInputStream fis = new FileInputStream("/Users/hongkai/Development/NswtGithub/NLPPIP/pagelist/2.txt");
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);

            /*FileOutputStream fos = new FileOutputStream("output.csv");
            fos.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);*/
        String line="";
        String[] towork = new String[0];
        int d = 0;

        // String[] titleArray = {};
        HanLP.Config.ShowTermNature = false;    // 关闭词性显示
        Segment segment = new CRFSegment().enableCustomDictionary(false);
        int ds = 0;
        while((line = br.readLine()) != null) {
            String[] split = br.readLine().split("\n");
            /*for(int i = 0 ; i< split.length;i++){
                List<Term> termList = segment.seg(split[i].toString());
                System.out.println(termList);
                List<String> phraseList = HanLP.extractPhrase(split[i].toString(), 100);
                System.out.println("短语==="+phraseList);
                List<String> keywordList = HanLP.extractKeyword(split[i].toString(), 5);
                System.out.println("关键词¥¥¥¥"+keywordList);

                //System.out.println(split[i].toString());
            }*/
            //System.out.println(ds++);

            for (String title : split)
            {
                suggester.addSentence(title);
            }

            d++;
            //bw.write(line);
            //bw.newLine();
        }
        //bw.close();
        br.close();

        //System.out.print(suggester.suggest("中国导航",2));
        System.out.print(suggester.suggest("杜绝抄袭",2));
        // System.out.println(suggester.suggest("国土资源", 2));
        // System.out.println(suggester.suggest("反洗钱内控",2));
        // System.out.println(suggester.suggest("为杜绝城市乱涂", 2));       // 语义
        //System.out.println(suggester.suggest("时间", 1));   // 字符
        //System.out.println(suggester.suggest("weidujuechengshituluan", 1));      // 拼音
        // System.out.println(br.readLine().split(",").toString());
    }
}

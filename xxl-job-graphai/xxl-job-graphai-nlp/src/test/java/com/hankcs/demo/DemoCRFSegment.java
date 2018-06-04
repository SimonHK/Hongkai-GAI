/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/10 22:02</create-date>
 *
 * <copyright file="DemoCRFSegment.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.demo;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * CRF分词(在最新训练的未压缩100MB模型下，能够取得较好的效果，可以投入生产环境)
 *
 * @author hankcs
 */
public class DemoCRFSegment
{
    public static void main(String[] args)
    {
        CRFAnalysis();
    }

    private static void CRFAnalysis() {
        HanLP.Config.ShowTermNature = false;    // 关闭词性显示
        Segment segment = new CRFSegment().enableCustomDictionary(false);
        BufferedReader bufferedReader = null;

        //File csv = new File("/Users/hongkai/Development/NswtGithub/NLPPIP/spiderfled/法律法规数据库(6).csv");  // CSV文件路径
        try {
            FileInputStream fis = new FileInputStream("/Users/hongkai/Development/NswtGithub/NLPPIP/spiderfled/法律法规数据库(6)out.txt");
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);

            /*FileOutputStream fos = new FileOutputStream("output.csv");
            fos.write(new byte[]{(byte)0xEF, (byte)0xBB, (byte)0xBF});
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            BufferedWriter bw = new BufferedWriter(osw);*/
            String line="";
            String[] towork = new String[0];
            int d = 0;
            while((line = br.readLine()) != null) {
                String[] split = br.readLine().split(",");
                for(int i = 0 ; i< split.length;i++){
                    List<Term> termList = segment.seg(split[i].toString());
                    System.out.println(termList);
                    List<String> phraseList = HanLP.extractPhrase(split[i].toString(), 100);
                    System.out.println("短语==="+phraseList);
                    List<String> keywordList = HanLP.extractKeyword(split[i].toString(), 5);
                    System.out.println("关键词¥¥¥¥"+keywordList);

                    //System.out.println(split[i].toString());
                }

               // System.out.println(br.readLine().split(",").toString());
                //towork[d] = br.readLine().toLowerCase();
                d++;
                //bw.write(line);
                //bw.newLine();
            }
            //bw.close();
            br.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }

      /*  String[] sentenceArray = new String[]
                {
                        "HanLP是由一系列模型与算法组成的Java工具包，目标是普及自然语言处理在生产环境中的应用。",
                        "鐵桿部隊憤怒情緒集結 馬英九腹背受敵",           // 繁体无压力
                        "馬英九回應連勝文“丐幫說”：稱黨內同志談話應謹慎",
                        "高锰酸钾，强氧化剂，紫红色晶体，可溶于水，遇乙醇即被还原。常用作消毒剂、水净化剂、氧化剂、漂白剂、毒气吸收剂、二氧化碳精制剂等。", // 专业名词有一定辨识能力
                        "《夜晚的骰子》通过描述浅草的舞女在暗夜中扔骰子的情景,寄托了作者对庶民生活区的情感",    // 非新闻语料
                        "这个像是真的[委屈]前面那个打扮太江户了，一点不上品...@hankcs",                       // 微博
                        "鼎泰丰的小笼一点味道也没有...每样都淡淡的...淡淡的，哪有食堂2A的好次",
                        "克里斯蒂娜·克罗尔说：不，我不是虎妈。我全家都热爱音乐，我也鼓励他们这么做。",
                        "今日APPS：Sago Mini Toolbox培养孩子动手能力",
                        "财政部副部长王保安调任国家统计局党组书记",
                        "2.34米男子娶1.53米女粉丝 称夫妻生活没问题",
                        "你看过穆赫兰道吗",
                        "国办发布网络提速降费十四条指导意见 鼓励流量不清零",
                        "乐视超级手机能否承载贾布斯的生态梦",
                        "王大虎是个技术人员，在博雅软件和新宇联安都待过。主要做监管系统，是监管部门的主管。其产品是1104、反洗钱、企业征信、监管报表等。"
                };
        //text = "王大虎是个技术人员，在博雅软件和新宇联安都待过。主要做监管系统，是监管部门的主管。其产品是1104、反洗钱、企业征信、监管报表等。";
        for (String sentence : sentenceArray)
        {
            List<Term> termList = segment.seg(sentence);
            System.out.println(termList);
        }*/

        /**
         * 内存CookBook:
         * HanLP内部有智能的内存池，对于同一个CRF模型（模型文件路径作为id区分），只要它没被释放或者内存充足，就不会重新加载。
         */
        for (int i = 0; i < 5; ++i)
        {
            segment = new CRFSegment();
        }
    }
}

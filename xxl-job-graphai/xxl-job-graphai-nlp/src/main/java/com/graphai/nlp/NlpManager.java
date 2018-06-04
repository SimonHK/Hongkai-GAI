package com.graphai.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.suggest.Suggester;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NlpManager {

    public static void main(String [] args){

        try {
            testFinder();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static void testFinder() throws IOException {
        Suggester suggester = new Suggester();
        FileInputStream fis = new FileInputStream("/Users/hongkai/Development/GitHubSource/xxl-job/data/pagelist/2.txt");
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

        System.out.print(suggester.suggest("中国导航",2));
        //System.out.print(suggester.suggest("杜绝抄袭",2));
        // System.out.println(suggester.suggest("国土资源", 2));
        // System.out.println(suggester.suggest("反洗钱内控",2));
        // System.out.println(suggester.suggest("为杜绝城市乱涂", 2));       // 语义
        //System.out.println(suggester.suggest("时间", 1));   // 字符
        //System.out.println(suggester.suggest("weidujuechengshituluan", 1));      // 拼音
        // System.out.println(br.readLine().split(",").toString());
    }
}

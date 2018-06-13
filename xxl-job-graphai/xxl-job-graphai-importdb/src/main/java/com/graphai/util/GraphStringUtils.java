package com.graphai.util;

import com.mysql.jdbc.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ThreadLocalRandom;

public class GraphStringUtils {


    public static String formatRepetition(String stris){
        StringBuffer sb = new StringBuffer("");
        HashSet<String> hs = new HashSet<String>();
        if(!StringUtils.isNullOrEmpty(stris)) {
            String[] split = stris.split(",");
            for(String sp:split) {
                hs.add(sp);
            }
            Iterator<String> i = hs.iterator();
            while (i.hasNext()) {
                //System.out.println(i.next());
                sb.append(i.next()).append(",");
            }
        }
        return  sb.toString();
    }


    public static void main(String[] args){

       // String str = "定增,紫光,紫光,紫光,高管,任非,高管增,旷达,董监,高及,旷达,沈介良,旷达,董监,伟明,伟,伟明,广泽,高管,管任松,陈运白,高管,刘宗尚,胡彦超,郭永来,董监,董监,陈宁,周海华,叶枫,伟计划,易明,易明,嘉泽创,永利,潘刚,潘刚,占回购,丹化,明起,丹化,周建灿,周氏,康宏,康宏,康宏,嘉楠,耘智,耘智,兴源,兴源,兴源,华兰,鲁泰,鲁泰,";
        //System.out.println(formatRepetition(str));

        long[] arrayOfLong = new long [ 20000 ];

        Arrays.parallelSetAll( arrayOfLong,index -> ThreadLocalRandom.current().nextInt( 1000000 ) );
        Arrays.stream( arrayOfLong ).limit( 10 ).forEach(i -> System.out.print( i + " " ) );
        System.out.println();

        Arrays.parallelSort( arrayOfLong );
        Arrays.stream( arrayOfLong ).limit( 10 ).forEach(i -> System.out.print( i + " " ) );
        System.out.println();
    }


}

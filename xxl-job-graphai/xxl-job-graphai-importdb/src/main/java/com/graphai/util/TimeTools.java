package com.graphai.util;

import java.text.SimpleDateFormat;

public class TimeTools {


    public static String getNowTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowTime = df.format(System.currentTimeMillis());
        return nowTime;
    }

}

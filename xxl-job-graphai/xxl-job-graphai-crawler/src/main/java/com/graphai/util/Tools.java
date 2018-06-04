package com.graphai.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {


    public static boolean isFindUrl(String url, String regex) {
        //Scanner sc = new Scanner(System.in);
        //String input = sc.nextLine();
        //String regex = "^[\\u4e00-\\u9fa5]*$";
        Matcher m = Pattern.compile(regex).matcher(url);
        return m.find();
    }


    public static void main(String[] args){
        String s = "http://finance.sina.co我m.cn";
        String regexStr = "[\u4E00-\u9FA5]";
        boolean findUrl = isFindUrl(s, regexStr);
        System.out.print(findUrl);
    }


}

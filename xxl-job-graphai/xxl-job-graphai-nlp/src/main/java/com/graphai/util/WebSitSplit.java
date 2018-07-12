package com.graphai.util;

public class WebSitSplit {

    public String websitsplit(WebSitSplitEnum s)
    {
        String splitstring = "";
        switch(s)
        {
            case SOHU:
                splitstring="</p>";
                break;
            case SINA:
                splitstring="</p>";
                break;
            case OTHER:
                splitstring="</p>";
                break;
        }
        return splitstring;
    }

    public static void main(String[] args) {
        WebSitSplitEnum s = WebSitSplitEnum.SINA;
        String name = s.getName();
        System.out.print(name);
        WebSitSplit test = new WebSitSplit();
        String websitsplit = test.websitsplit(s);
        System.out.print(websitsplit);
    }
}
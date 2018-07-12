package com.graphai.util;

public enum WebSitSplitEnum {



    SOHU("www.sohu.com"),
    SINA("sina"),
    OTHER("other");

    private final String name;

    private WebSitSplitEnum(String name)
    {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

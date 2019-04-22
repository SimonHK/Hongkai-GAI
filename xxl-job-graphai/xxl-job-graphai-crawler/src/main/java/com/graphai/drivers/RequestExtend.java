package com.graphai.drivers;

import us.codecraft.webmagic.Request;

/**
 *
 * @author zhuangj
 * @date 2017/11/15
 */
public class RequestExtend extends Request {

    private String parentUrl;

    public RequestExtend(String url,String parentUrl) {
        super(url);
        this.parentUrl = parentUrl;
    }


    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }
}
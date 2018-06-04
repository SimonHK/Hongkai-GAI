package com.graphai.model;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author code4crafter@gmail.com <br>
 */
public class SoHuProcessor implements PageProcessor {

    public static final String URL_LIST = "http://www\\.sohu\\.com\\/c/8/1460";

    public static final String URL_POST = "http://www.sohu.com/a/\\*";

    private Site site = Site
            .me()
            .setDomain("www.sohu.com")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        //列表页
        if (page.getUrl().regex(URL_LIST).match()) {
            page.addTargetRequests(page.getHtml().xpath("//div[@data-role=\"news-item\"]").links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
            //文章页
        } else {
            page.putField("title", page.getHtml().xpath("//div[@class='text-title']/h1"));
            page.putField("content", page.getHtml().xpath("//p"));
            page.putField("date",page.getHtml().xpath("//span[@id='news-time']//span[@class='time']").regex("\\((.*)\\)"));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new SoHuProcessor()).addUrl("http://www.sohu.com/c/8/1460")
                .run();
    }
}

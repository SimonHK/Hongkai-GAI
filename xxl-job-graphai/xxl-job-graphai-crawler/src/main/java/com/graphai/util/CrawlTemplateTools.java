package com.graphai.util;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

public class CrawlTemplateTools {

    /**
     * 新浪网站数据爬取模版。
     * 定义新浪网站的爬取信息规则。
     * @param page //当前爬取的页面对象。
     * @param pageType //网站类型入，新浪（sina），搜狐(sohu)....。
     * */
    protected static void CrawTemplate(Page page,String pageType) {
        List<String> getH1 = new ArrayList<String>(0);
        Selectable getAllPage = null;
        List<String> getAllText = new ArrayList<String>(0);
        Selectable urls = null;
        Selectable getReleaseTime = null;
        List<String> getPageTitle = null;
        if("sina".equals(pageType)) {
            //标题内容
            getH1 = page.getHtml().xpath("//h1/text()").all();
            //页面全内容
            getAllPage = page.getHtml().xpath("/");
            //获取为P标签下的内容文本
            getAllText = page.getHtml().xpath("//p/text()").all();
            //获取当前页面中所有URL
            urls = page.getHtml().xpath("//a[@href]").links();
            //获取当前页面时间
            getReleaseTime = page.getHtml().xpath("//[@class='date']");
            //getReleaseTime = page.getHtml().xpath("//[@class='b_time']");//新浪的这部分时间是专题页面中有的
            //获取当前页面的title
            getPageTitle = page.getHtml().xpath("//title/text()").all();
        }

        /*StringBuffer sb = new StringBuffer("");
        List<Selectable> urlnodes = urls.nodes();
        for (Selectable selectable : urlnodes) {
            //剔除中文
            String urltitle = selectable.toString().replaceAll("[^\u4e00-\u9fa5]", "");
            String url = selectable.links().toString().trim();
            if (StringUtils.isEmpty(urltitle)) {
                urltitle = "没有title";
            }
            if (StringUtils.isNotEmpty(url)) {
                sb.append(urltitle.concat("--").concat(url).concat(";"));
            }
        }*/
        //Selectable links = urls.links();
        //page.putField("htmlurl", sb.toString());
        page.putField("getH1",getH1);
        page.putField("getAllPage",getAllPage);
        page.putField("getAllText",getAllText);
        page.putField("urls",urls);
        page.putField("getReleaseTime",getReleaseTime);
        page.putField("getPageTitle",getPageTitle);
    }

}

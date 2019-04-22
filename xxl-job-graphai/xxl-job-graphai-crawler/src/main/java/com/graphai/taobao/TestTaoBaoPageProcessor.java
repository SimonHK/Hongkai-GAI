package com.graphai.taobao;


import com.graphai.drivers.ChromeDriver;
import com.graphai.drivers.Proxy.ProxyIp;
import com.graphai.drivers.PtDriver;
import com.graphai.drivers.RequestExtend;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author zhuangj
 * @date 2017/11/13
 */
public class TestTaoBaoPageProcessor implements PageProcessor {

    private String keyWord;

    private static final String TAO_BAO_DETAIL_URL_START="https://item.taobao.com/item.htm";

    private static final String TIAN_MAO_DETAIL_URL_START="https://detail.tmall.com/item.htm";

    private Site site = Site
            .me()
            .setCharset("UTF-8")
            .setCycleRetryTimes(3)
            .setSleepTime(3 * 1000)
            .addHeader("Connection", "keep-alive")
            .addHeader("Cache-Control", "max-age=0")
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0");

/*

    public String getKeyWord() {
        return keyWord;
    }
*/
    /**
     * 使用chromeDriver程序正常运行,转换成PhtanomJs后发现查询到的数据不是想要的数据，复制HTML查看页面后,
     * 发现搜索的数据是错乱的,搜索框上显示着？？？，猜测是转码的问题，经过URLEncode之后，程序正常运行。
     * @return
     */
    public String getKeyWord() {
        try {
            return URLEncoder.encode(keyWord,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }


    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public TestTaoBaoPageProcessor() {
    }

    public TestTaoBaoPageProcessor(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public Site getSite() {
        return site;
    }




    @Override
    public void process(Page page) {
        WebDriver driver= PtDriver.getPhantomJSDriver();
        /*WebDriver driver= null;
        try {
            driver = ChromeDriver.getChromeDriver();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        driver.get(page.getRequest().getUrl());
        WebElement webElement = driver.findElement(By.id("page"));
        String str = webElement.getAttribute("outerHTML");

        Html html = new Html(str);
        if(isFirstPage(html)){
            analysisPagination(page,html);
        }else if(isListPage(html)){
            analysisListPage(page,html,driver);
        }else {
            analysisDetailPage(page,html,driver);
        }
        //driver.close();
    }

    /*private void analysisListPage(Page page, Html html, WebDriver driver) {
        List<String> detailPageList= html.xpath("//*[@id=\"mainsrp-itemlist\"]").$("a[id^='J_Itemlist_TLink_']").xpath("//a/@href").all();
        page.addTargetRequests(detailPageList);
    }*/

    private void analysisListPage(Page page, Html html, WebDriver driver) {
        RequestExtend requestExtend= (RequestExtend) page.getRequest();
        System.out.println("parentUrl:"+requestExtend.getParentUrl());
        System.out.println("nowUrl:"+page.getUrl());
        List<String> detailPageList= html.xpath("//*[@id=\"mainsrp-itemlist\"]").$("a[id^='J_Itemlist_TLink_']").xpath("//a/@href").all();
        for(String deetailPage:detailPageList){
            RequestExtend extend=new RequestExtend("https:"+deetailPage,page.getUrl().toString());
            page.addTargetRequest(extend);
        }
        //driver.close();
    }


    /**
     * 分析分页规则
     * @param page
     * @param html
     */
    private void analysisPagination(Page page,Html html){
        List<String> pageList= html.xpath("//*[@id=\"mainsrp-pager\"]/div/div/div/ul/li/a/@data-value").all();
        pageList = new ArrayList(new HashSet(pageList));

        List<String> pageParameterList=new ArrayList<String>();
        for(String  value:pageList){
            pageParameterList.add("https://s.taobao.com/search?q="+getKeyWord()+"&s="+value);
        }
        page.addTargetRequests(pageParameterList);
    }

    /**
     * 分析详情页
     * @param page
     * @param html
     * @param driver
     */
    private void analysisDetailPage(Page page,Html html,WebDriver driver){
        String url=page.getUrl().toString();
        if(url.startsWith(TAO_BAO_DETAIL_URL_START)){
            analysisTaoBaoDetailPage(page,html,driver);
        }else if(url.startsWith(TIAN_MAO_DETAIL_URL_START)){
            analysisTianMaoDetailPage(page,html,driver);
        }
        driver.close();
    }

    /**
     * 分析淘宝详情页
     * @param page
     * @param html
     * @param driver
     */
   /* private void analysisTaoBaoDetailPage(Page page,Html html,WebDriver driver){
        page.putField("price", html.xpath("//[@id=\"J_StrPrice\"]/em[2]/text()").toString());
        page.putField("shopName", html.xpath("//*[@id=\"J_ShopInfo\"]/div/div[1]/div[1]/dl/dd/strong/a/text()").toString());
        page.putField("title", html.xpath("////*[@id=\"J_Title\"]/h3/text()").toString());

    }*/

    /**
     * 分析淘宝详情页
     * @param page
     * @param html
     * @param driver
     */
    private void analysisTaoBaoDetailPage(Page page,Html html,WebDriver driver){
        RequestExtend requestExtend= (RequestExtend) page.getRequest();
        System.out.println("parentUrl:"+requestExtend.getParentUrl());
        System.out.println("nowUrl:"+page.getUrl());
        page.putField("price", html.xpath("//[@id=\"J_StrPrice\"]/em[2]/text()").toString());
        String shopName=html.xpath("//*[@id=\"J_ShopInfo\"]/div/div[1]/div[1]/dl/dd/strong/a/text()").toString();
        if(StringUtils.isBlank(shopName)){
            shopName=html.xpath("//*[@id=\"header-content\"]/div[2]/div[1]/div[1]/a/text()").toString();
        }
        page.putField("shopName", shopName);
        page.putField("title", html.xpath("////*[@id=\"J_Title\"]/h3/text()").toString());

        TaoBaoPo po=new TaoBaoPo();
        po.setParentUrl(requestExtend.getParentUrl());
        po.setPrice(page.getResultItems().get("price").toString());
        po.setShopName(page.getResultItems().get("shopName").toString());
        po.setTitle(page.getResultItems().get("title").toString());
        po.setUrl(page.getUrl().toString());
        System.out.println("TITLE:"+po.getTitle());
        System.out.println("URL:"+po.getUrl());
        System.out.println("ParentURL:"+po.getParentUrl());
        System.out.println("ShopName:"+po.getShopName());
        System.out.println("Price:"+po.getPrice());



        //taoBaoCrawlerService.saveProductDetail(po);
    }



    /**
     * 分析天猫详情页
     * @param page
     * @param html
     * @param driver
     */
    private void analysisTianMaoDetailPage(Page page,Html html,WebDriver driver){
        page.putField("price", html.xpath("//[@id=\"J_StrPriceModBox\"]/dd/span/text()").toString());
        page.putField("shopName", driver.findElement(By.name("seller_nickname")).getAttribute("value"));
        page.putField("name", html.xpath("//[@id=\"J_DetailMeta\"]/div[1]/div[1]/div/div[1]/h1/text()").toString());
    }



    /**
     * 是否为列表页
     * @param html
     * @return
     */
    private boolean isListPage(Html html) {
        String tmp = html.$("#mainsrp-pager").get();
        if (StringUtils.isNotBlank(tmp)) {
            return true;
        }
        return false;
    }

    /**
     * 列表页获取当前页码
     * @param html
     * @return
     */
    private String getCurrentPageNo(Html html){
        return html.xpath("//*[@id=\"mainsrp-pager\"]/div/div/div/ul/li[contains(@class,'active')]/span/text()").toString();
    }

    /**
     * 判断是否列表页的第一页
     * @param html
     * @return
     */
    private Boolean isFirstPage(Html html){
        return isListPage(html)&&getCurrentPageNo(html).equals("1");
    }



    public static void main(String[] args) {
        testTaoBaoSearch("java");
    }

    public static void testTaoBaoSearch(String keyWord){
        Spider spider=Spider.create(new TestTaoBaoPageProcessor(keyWord));
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        //免费代理不稳定老挂
        ProxyIp.setProxy();
        //httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("0.0.0.0",0000)));
        spider.setDownloader(httpClientDownloader);
        spider.addUrl("https://s.taobao.com/search?q="+keyWord).thread(1).run();
    }






}

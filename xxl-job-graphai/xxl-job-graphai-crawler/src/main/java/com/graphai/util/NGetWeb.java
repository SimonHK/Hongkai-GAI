package com.graphai.util;

import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.util.*;

public class NGetWeb implements PageProcessor {
    public static final String index_list = "http://finance.sina.com.cn";//校验地址正则

    // 部分一：抓取网站的相关配置，包括编码、重试次数、抓取间隔、超时时间、请求消息头、UA信息等
    private Site site = Site
            .me().setRetryTimes(3).setTimeOut(6000)
            .addHeader("Accept-Encoding", "/")
            .setDomain("www.sina.com")
            .setSleepTime(3000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    public String pageType = "";

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }
    public void process(Page page) {
        //setPageType("sina");
        List<String> getH1 = new ArrayList<String>(0);
        List<String> getAllPage = new ArrayList<>(0);
        List<String> getAllText = new ArrayList<String>(0);
        List<String> urls = new ArrayList<>(0);
        List<String> getReleaseTime  = new ArrayList<>(0);
        List<String> getPageTitle = new ArrayList<>(0);
        if (page.getUrl().regex(index_list).match()) {
            //标题内容
            getH1 = page.getHtml().xpath("//h1/text()").all();
            //页面全内容
            getAllPage = page.getHtml().xpath("/").all();
            //获取为P标签下的内容文本
            getAllText = page.getHtml().xpath("//p/text()").all();
            //获取当前页面中所有URL
            urls = page.getHtml().xpath("//a[@href]").links().all();
            //获取当前页面时间
            getReleaseTime = page.getHtml().xpath("//[@class='date']").all();
            //getReleaseTime = page.getHtml().xpath("//[@class='b_time']");//新浪的这部分时间是专题页面中有的
            //获取当前页面的title
            getPageTitle = page.getHtml().xpath("//title/text()").all();
        }else{
            page.setSkip(true);
        }
        page.putField("getH1",getH1);
        page.putField("getAllPage",getAllPage);
        page.putField("getAllText",getAllText);
        page.putField("urls",urls);
        page.putField("getReleaseTime",getReleaseTime);
        page.putField("getPageTitle",getPageTitle);
    }

    public Site getSite() {
        return this.site;
    }

    public static Map<String, String> getPageView(String url) {

        //url = "http://news.sina.com.cn";
        //爬虫抽取
        Map<String, String> mpv = new HashMap<>();
        //if(url.matches("\\[+sina.com+\\]")) {
        /*if("www.nanfangdaily.com.cn".equals(url.trim())){
            System.out.println("");
        }*/
        //Spider spider = Spider.create(new GithubRepoPageProcessor()).addUrl("https://github.com/code4craft").thread(1);

        //System.setProperty("selenuim_config", "/Users/hongkai/Development/GitHubSource/xxl-job/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/drivers/mac/selenuimconfig.cfg");
        Spider spider = Spider.create(new NGetWeb()).thread(1);
                //.setDownloader(new SeleniumDownloader("/Users/hongkai/Development/GitHubSource/xxl-job/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/drivers/mac/chromedriver"))

        HttpClientDownloader downloader = new HttpClientDownloader() {
            @Override
            protected void onError(Request request) {
                setProxyProvider(SimpleProxyProvider.from(new Proxy("10.10.10.10", 8888)));
            }
        };
        spider.setDownloader(downloader);
        //RedisScheduler scheduler = new RedisScheduler(new JedisPool("127.0.0.1", 6379));
//    	FileCacheQueueScheduler scheduler = new FileCacheQueueScheduler("urls");
//    	QueueScheduler scheduler = new QueueScheduler();
        //spider.setScheduler(scheduler);


        ArrayList<SpiderListener> listeners = new ArrayList<>();
        listeners.add(new SpiderListener() {
            @Override
            public void onSuccess(Request request) {
            }

            @Override
            public void onError(Request request) {
                Integer cycleTriedTimes =
                        (Integer) request.getExtra(Request.CYCLE_TRIED_TIMES);
                request.putExtra(Request.CYCLE_TRIED_TIMES,
                        cycleTriedTimes == null ? 1 : cycleTriedTimes + 1);
                //spider.addRequest(request);
            }
        });
        spider.setSpiderListeners(listeners);



        String urlTemplate = url;
        List<String> storeurls = new ArrayList<>(10);
        ResultItems resultItems = new ResultItems();
        try {
            resultItems = spider.get(urlTemplate);

        } catch (Exception e) {
            System.out.print("爬取：【"+urlTemplate+"】链接的内容失败！！！");
        }
        if(resultItems != null) {
            //处理副标题
            List<String> getH1 = resultItems.get("getH1");
            StringBuffer sbH1 = new StringBuffer("");
            for(String str:getH1){
                if(StringUtils.isNotEmpty(str)){
                    sbH1.append(str).append(";");
                }
            }
            mpv.put("AllH1", sbH1.toString());

            //处理所有页面
            String getAllPage  = resultItems.get("getAllPage").toString();
            mpv.put("AllPage",getAllPage);

            //处理所有URLs
            List<String> allurls = resultItems.get("urls");
            StringBuffer sbUrls = new StringBuffer("");
            for(String str:allurls){
                if(StringUtils.isNotEmpty(str)){
                    sbUrls.append(str).append(";");
                }
            }
            //处理获取的URL内容，去除重复url包括当前已经爬取的url
            String[] split = sbUrls.toString().split(";");
            String[] strings = array_unique(split);
            StringBuffer nsburl = new StringBuffer("");
            for (String uniques : strings) {
                nsburl.append(uniques.concat(";"));
            }
            mpv.put("AllUrls", nsburl.toString());

            //处理文章发布时间
            List<String> all = resultItems.get("getReleaseTime");
            StringBuffer newReleaseTime = new StringBuffer("");
            for(String str:all){
                if(StringUtils.isNotEmpty(str)){
                    newReleaseTime.append(str).append(";");
                }
            }
            mpv.put("ReleaseTime", newReleaseTime.toString());

            //获取页面标题并进行处理
            List<String> getPageTitle = resultItems.get("getPageTitle");
            StringBuffer sbPageTitle = new StringBuffer("");
            for(String str:getPageTitle){
                if(StringUtils.isNotEmpty(str)){
                    sbPageTitle.append(str).append(";");
                }
            }
            mpv.put("PageTitle", sbPageTitle.toString());

            //处理以标签P爬取的内容进行初步处理
            List<String> getAllText = resultItems.get("getAllText");
            StringBuffer sbAllText = new StringBuffer("");
            for(String str:getAllText){
                if(StringUtils.isNotEmpty(str)){
                    sbAllText.append(str).append("\n");
                }
            }
            mpv.put("AllText",sbAllText.toString());
        }
        return mpv;

    }



    static Map<Integer, Map<String, String>> urlMap = new HashMap<Integer, Map<String, String>>();

    /***
     * @param url 第一层Url
     * @param currentDeth 当前深度 从1开始
     * @param maxDeth 抓取最大深度
     */
    public static void getUrls1(String url, int currentDeth, int maxDeth) {
        if (currentDeth >= maxDeth) {
            return;
        }
        Map<String, String> pageView = new HashMap<String, String>();
        List<String> urls = new ArrayList<String>();
        if (currentDeth == 1) {
            urls.add(url);
        } else {
            Map<String, String> map = urlMap.get(currentDeth);
            for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
                String url1 = (String) iterator.next();
                urls.add(url1);
            }
        }

        for (Iterator iterator = urls.iterator(); iterator.hasNext();) {
            String url2 = (String) iterator.next();
            try {
                //等待5秒后爬取下一个网页
                System.out.println("**************************************************");
                System.out.println("等待5秒"+new Date());
                System.out.println("**************************************************");
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pageView = NGetWeb.getPageView(url2);
            String allText = StringUtils.isEmpty(pageView.get("AllText"))?"":pageView.get("AllText");
            String pageTitle = StringUtils.isEmpty(pageView.get("PageTitle"))?"":pageView.get("PageTitle");
            String releaseTime = StringUtils.isEmpty(pageView.get("ReleaseTime"))?"":pageView.get("ReleaseTime");
            String allPage = StringUtils.isEmpty(pageView.get("AllPage"))?"":pageView.get("AllPage");
            String allH1 = StringUtils.isEmpty(pageView.get("AllH1"))?"":pageView.get("AllH1");
            String allUrls = StringUtils.isEmpty(pageView.get("AllUrls"))?"":pageView.get("AllUrls");
            System.out.println("============================================");
            System.out.println("页面标题：【" + pageTitle+"】");
            System.out.println("============================================");
            System.out.println("页面真实时间:【" + releaseTime+"】");
            System.out.println("============================================");
            System.out.println("页面副标题:【" + allH1+"】");
            System.out.println("============================================");
            System.out.println("页面URL集合：【" + allUrls+"】");
            System.out.println("============================================");
            System.out.println("页面文本内容:【" + allText+"】");
            System.out.println("============================================");
            System.out.println("页面所有内容：【：" + allPage+"】");
            System.out.println("============================================");
            if (pageView.get(currentDeth + 1) == null) {
                urlMap.put((currentDeth + 1), new HashMap<String, String>());
            }
            if (allUrls != null) {
                for (int i = 0; i < allUrls.split(";").length; i++) {
                    urlMap.get(currentDeth + 1).put(allUrls.split(";")[i], "");
                }
            }
        }
        getUrls1("", (currentDeth + 1), maxDeth);
    }



    public static void main(String[] args) {
        getUrls1("http://finance.sina.com.cn/", 1, 3);
        System.out.print("!");
    }

    /**
     * 字符串过滤特殊符号处理
     */
    public static String filterString(String str) {
        return str.trim().replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+", "");
    }

    /**
     * 重复地址过滤
     */
    public static String[] array_unique(String[] a) {
        Set<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(a));
        return set.toArray(new String[0]);
    }

    /**
     * 处理特殊字符非标点符号。
     * */
    public static String pfilterString(String str){
        return str.trim().replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】\\s+\\s*\\t]+", "");
    }

}

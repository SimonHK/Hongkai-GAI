package com.graphai.util;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.selector.Selectable;

import java.util.*;

public class GetWebUrl implements PageProcessor {

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
        //获取当前页面中所有URL
        List<String> allurl = new ArrayList<>(0);
        if (page.getUrl().regex(index_list).match()) {
            allurl = page.getHtml().xpath("//a[@href]").links().all();
        }
        page.putField("urls", allurl);
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
        //System.setProperty("selenuim_config", "/Users/hongkai/Development/GitHubSource/xxl-job/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/drivers/mac/selenuimconfig.cfg");
        final Spider spider = Spider.create(new GetWebUrl()).thread(2)
                //.setDownloader(new SeleniumDownloader("/Users/hongkai/Development/GitHubSource/xxl-job/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/drivers/mac/chromedriver"))
                ;
        //Spider spider = Spider.create(new GithubRepoPageProcessor()).addUrl("https://github.com/code4craft").thread(1);
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
                spider.addRequest(request);
            }
        });
        spider.setSpiderListeners(listeners);


        String urlTemplate = url;
        List<String> storeurls = new ArrayList<>(10);
        ResultItems resultItems = new ResultItems();
        try {
            if (Tools.isFindUrl(urlTemplate, index_list)) {
                resultItems = (ResultItems) spider.get(urlTemplate);
            }
        } catch (Exception e) {
            System.out.print("爬取：【" + urlTemplate + "】链接的内容失败！！！");
        }
        if (resultItems.getAll().size() > 0) {
            //处理所有URLs
            List<String> allurls = resultItems.get("urls");
            //List<String> allurls = urls.all();
            StringBuffer sbUrls = new StringBuffer("");
            for (String str : allurls) {
                if (StringUtils.isNotEmpty(str)) {
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

        }
        // }
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
            for (Iterator iterator = map.keySet().iterator(); iterator.hasNext(); ) {
                String url1 = (String) iterator.next();
                urls.add(url1);
            }
        }

        for (Iterator iterator = urls.iterator(); iterator.hasNext(); ) {
            String url2 = (String) iterator.next();
           /* try {
                //等待5秒后爬取下一个网页
                System.out.println("**************************************************");
                System.out.println("等待5秒");
                System.out.println("**************************************************");
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            pageView = GetWebUrl.getPageView(url2);
            String allUrls1 = pageView.get("AllUrls");
            String allUrls = "";
            if (StringUtils.isNotEmpty(allUrls1)) {
                allUrls = pageView.get("AllUrls").trim();
            }
            System.out.println("============================================");
            System.out.println("页面URL集合：【" + allUrls + "】");
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

        getUrls1("http://finance.sina.com.cn/", 1, 4);
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
     */
    public static String pfilterString(String str) {
        return str.trim().replaceAll("[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】\\s+\\s*\\t]+", "");
    }

}

package com.graphai.job;

import com.graphai.dao.CrawlerUrlsDao;
import com.graphai.dao.CrawlercontentDao;
import com.graphai.dao.RedisDao;
import com.graphai.pojo.Crawlercontent;
import com.graphai.pojo.Crawlerurls;
import com.graphai.util.DataSourceContextHolder;
import com.graphai.util.NGetWeb;
import com.graphai.util.UuidUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.graphai.util.NGetWeb.pfilterString;


/**
 * 任务Handler示例（Bean模式）
 *
 * 开发步骤：
 * 1、继承"IJobHandler"：“com.xxl.job.core.handler.IJobHandler”；
 * 2、注册到Spring容器：添加“@Component”注解，被Spring容器扫描为Bean实例；
 * 3、注册到执行器工厂：添加“@JobHandler(value="自定义jobhandler名称")”注解，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 4、执行日志：需要通过 "XxlJobLogger.log" 打印执行日志；
 *
 * @author xuxueli 2015-12-19 19:43:36
 */
@JobHandler(value="crawlerJob")
@Component
public class CrawlerJob extends IJobHandler {

    @Resource
    private CrawlercontentDao crawlercontentDao;
    @Resource
    private CrawlerUrlsDao crawlerUrlsDao;
    @Resource
    private RedisDao redisDao;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        //测试代码段开始
        //String url  = "http://news.sina.com.cn/w/2018-04-21/doc-ifznefkf9744275.shtml";
        //InCrawlercontent(url);
        //测试代码段结束

       /* redisDao.addString("1","你好");

        String s = redisDao.get("1");
        System.out.println(s);*/

        int depCrawler = 4;
        List<String> urls = getUrls();
        boolean empty = StringUtils.isEmpty(param);
        String depFindUrls = "";
        //从指定网站内爬去

        if(!empty){
            getWebPage(param,1,depCrawler);
        }else {
            //从数据库存取的URL地址列表中爬取
            for (String url : urls) {
                getWebPage(url,1,depCrawler);
            }
        }

        return SUCCESS;
    }


    static Map<Integer, Map<String, String>> urlMap = new HashMap<Integer, Map<String, String>>();

    /***
     * @param url 第一层Url
     * @param currentDeth 当前深度 从1开始
     * @param maxDeth 抓取最大深度
     */
    public void getWebPage(String url, int currentDeth, int maxDeth) {
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
                Thread.currentThread().sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pageView = NGetWeb.getPageView(url2);
            String allText = pageView.get("AllText").trim();
            String pageTitle = pageView.get("PageTitle").trim();
            String releaseTime = pageView.get("ReleaseTime").trim();
            String allPage = pageView.get("AllPage").trim();
            String allH1 = pageView.get("AllH1").trim();
            String allUrls = pageView.get("AllUrls").trim();
            System.out.println("============================================");
            System.out.println("页面标题：【" + pageTitle+"】");
            System.out.println("============================================");
            System.out.println("页面真实时间:【" + releaseTime+"】");
            System.out.println("============================================");
            System.out.println("页面副标题:【" + allH1+"】");
            System.out.println("============================================");
            System.out.println("页面URL集合：【" + allUrls+"】");
            System.out.println("============================================");
           // System.out.println("页面文本内容:【" + allText+"】");
           // System.out.println("============================================");
            //System.out.println("页面所有内容：【：" + allPage+"】");
           // System.out.println("============================================");
            if (pageView.get(currentDeth + 1) == null) {
                urlMap.put((currentDeth + 1), new HashMap<String, String>());
            }
            if (allUrls != null) {
                for (int i = 0; i < allUrls.split(";").length; i++) {
                    urlMap.get(currentDeth + 1).put(allUrls.split(";")[i], "");
                }
            }
            //存储页面内容
            String pagecontent = allText.trim();
            Crawlercontent crawlercontent = new Crawlercontent();
            crawlercontent.setId(UuidUtil.get32UUID());
            crawlercontent.setCrawlerurl(url);
            crawlercontent.setCrawlercontent(pfilterString(pagecontent.trim()));
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattime = sdf.format(d);
            crawlercontent.setCrawlertime(formattime);
            int save = 0;
            if(!org.springframework.util.StringUtils.isEmpty(crawlercontent.getCrawlercontent())){
                save = crawlercontentDao.save(crawlercontent);
            }
            XxlJobLogger.log("GraphAI-JOB,存储当前数据！来源：【" + url + "】");
            XxlJobLogger.log("GraphAI-JOB,存储当前数据！时间：【" + formattime + "】");
            XxlJobLogger.log("存储结果：【" + save + "】");

        }
        getWebPage("", (currentDeth + 1), maxDeth);
    }


    private List<String> getUrls(){
        DataSourceContextHolder.setDbType("ds1");
        List<Crawlerurls> crawlerurls = crawlerUrlsDao.pageList();
        ArrayList<String> strings = new ArrayList<>();
        for (int i  = 0  ; i<crawlerurls.size(); i++){
            Crawlerurls crawlerurls1 = crawlerurls.get(i);
            String crawlerurl = crawlerurls1.getCrawlerurl();
            strings.add(crawlerurl);
        }
        return strings;
    }

    public void restoreDefaultDataSource() {
        String dbType = DataSourceContextHolder.getDbType();
        if (!"ds0".equals(dbType)) {
            DataSourceContextHolder.setDbType("ds0");
        }
    }

}

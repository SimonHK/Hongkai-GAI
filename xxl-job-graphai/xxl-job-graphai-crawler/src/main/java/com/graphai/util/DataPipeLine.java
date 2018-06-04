package com.graphai.util;


import java.util.List;

import com.graphai.dao.CrawlerUrlsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;

@Component
public class DataPipeLine implements Pipeline {

    @Resource
    private CrawlerUrlsDao crawlerUrlsDao;
    /**
     * mysql 存储
     */
    /*
     * public void process(ResultItems resultItems, Task task) {
     * List<ProxyIp>proxyIpList = resultItems.get("proxyIpList");
     * if(proxyIpList!=null&&!proxyIpList.isEmpty()){
     * proxyIpService.saveProxyIpList(proxyIpList); }
     *
     * }
     */

    /**
     * redis 存储
     */
    public void process(ResultItems resultItems, Task task) {
        List<Object> proxyIpList = resultItems.get("proxyIpList");
        if (proxyIpList != null && !proxyIpList.isEmpty()) {
            //crawlerUrlsDao.saveProxyListIpInRedis(proxyIpList);
            crawlerUrlsDao.pageList();
        }

    }
}

package com.graphai.pojo;

public class Crawlerurls {
    /**
     DROP TABLE IF EXISTS `crawlerurls`;
     CREATE TABLE `crawlerurls` (
     `id` varchar(32) NOT NULL,
     `crawlerurl` varchar(2000) DEFAULT NULL,
     `crawletime` varchar(45) DEFAULT NULL,
     `urltype` varchar(45) DEFAULT NULL,
     PRIMARY KEY (`id`)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
     */

    private String id;

    private String crawlerurl;

    private String titile;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCrawlerurl() {
        return crawlerurl;
    }

    public void setCrawlerurl(String crawlerurl) {
        this.crawlerurl = crawlerurl;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }
}

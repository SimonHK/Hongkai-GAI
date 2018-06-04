package com.graphai.model;

public class Storeresult {


//
//    CREATE TABLE `storeresult` (
//            `id` int(20) NOT NULL AUTO_INCREMENT,
//  `datasource` varchar(500) DEFAULT NULL COMMENT '数据来源',
//            `timeofoccurrence` varchar(32) DEFAULT NULL,
//  `eventclassification` varchar(200) DEFAULT NULL,
//  `ventcontent` longtext,
//    PRIMARY KEY (`id`)
//)
    public String id;
    //数据来源
    public String datasource;
    //事件类型
    public String eventclassification;
    //事件内容
    public String ventcontent;
    //事件时间
    public String timeofoccurrence;
    //事件机构或者地域
    public String location;
    //事件摘要
    public String abstracttext;
    //数据入库时间
    public String indbtime;

    public String getIndbtime() {
        return indbtime;
    }

    public void setIndbtime(String indbtime) {
        this.indbtime = indbtime;
    }

    public String getAbstracttext() {
        return abstracttext;
    }

    public void setAbstracttext(String abstracttext) {
        this.abstracttext = abstracttext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getEventclassification() {
        return eventclassification;
    }

    public void setEventclassification(String eventclassification) {
        this.eventclassification = eventclassification;
    }

    public String getVentcontent() {
        return ventcontent;
    }

    public void setVentcontent(String ventcontent) {
        this.ventcontent = ventcontent;
    }

    public String getTimeofoccurrence() {
        return timeofoccurrence;
    }

    public void setTimeofoccurrence(String timeofoccurrence) {
        this.timeofoccurrence = timeofoccurrence;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

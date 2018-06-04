package com.graphai.model;

public class Locations {

    /*`locationid` varchar(32) NOT NULL COMMENT '机构组织地域id',
            `locationname` varchar(500) DEFAULT NULL COMMENT '机构地域组织名称',
            `locationalias` longtext COMMENT '机构组织或地域别名多可用逗号分开',*/

    public String locationid;

    public String locationname;

    public String locationalias;


    public String getLocationid() {
        return locationid;
    }

    public void setLocationid(String locationid) {
        this.locationid = locationid;
    }

    public String getLocationname() {
        return locationname;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    public String getLocationalias() {
        return locationalias;
    }

    public void setLocationalias(String locationalias) {
        this.locationalias = locationalias;
    }
}

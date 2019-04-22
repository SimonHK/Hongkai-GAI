package com.graphai.drivers.Proxy;

public class ProxyPo {
    private String id;
    private String ip;
    private String port;
    private String area;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getPort() {
        return port;
    }
    public void setPort(String port) {
        this.port = port;
    }
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public ProxyPo(String id, String ip, String port, String area) {
        super();
        this.id = id;
        this.ip = ip;
        this.port = port;
        this.area = area;
    }


}
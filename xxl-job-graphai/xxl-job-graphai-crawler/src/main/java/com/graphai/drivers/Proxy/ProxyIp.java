package com.graphai.drivers.Proxy;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ProxyIp {
    // private static final Logger log = Logger.getLogger(ProxyIp.class);
    // private static final String Continu = null;


    //为了突破IP限制需要动态替换代理ip。


    public static void setProxy() {
        // String str="";

        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("proxySet", "true");

        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以
        ProxyPo p1 = new ProxyPo("1", "41.231.53.41", "3128", "突尼斯");
        ProxyPo p2 = new ProxyPo("2", "114.112.91.135", "3128", "北京市");
        ProxyPo p3 = new ProxyPo("3", "111.161.126.83", "8080", "天津市 联通");
        ProxyPo p4 = new ProxyPo("4", "111.161.126.84", "80", "天津市 联通");
        ProxyPo p5 = new ProxyPo("5", "111.161.126.89", "8080", "天津市 联通");
        ProxyPo p6 = new ProxyPo("6", "111.161.126.85", "80", "天津市 ");
        ProxyPo p7 = new ProxyPo("7", "111.161.126.92", "8080", "突尼斯");
        ProxyPo p8 = new ProxyPo("8", "183.224.1.30", "80", "昆明");
        ProxyPo p9 = new ProxyPo("9", "111.161.126.88", "8080", "天津");
        ProxyPo p10 = new ProxyPo("10", "14.18.16.67", "80", "广州");
        ProxyPo p11 = new ProxyPo("11", "222.246.232.55", "80", "湖南");
        ProxyPo p12 = new ProxyPo("12", "220.181.32.106", "80", "北京");
        ProxyPo p13 = new ProxyPo("13", "202.108.23.247", "80", "北京");

        ProxyPo p14 = new ProxyPo("14", "106.3.40.249", "8081", "北京");
        ProxyPo p15 = new ProxyPo("15", "58.56.124.192", "80", "济南");
        ProxyPo p16 = new ProxyPo("16", "223.202.3.49", "8080", "北京");
        ProxyPo p17 = new ProxyPo("17", "218.4.236.117", "80", "江苏");
        ProxyPo p18 = new ProxyPo("18", "120.210.202.4", "80", "安徽");
        ProxyPo p19 = new ProxyPo("19", "121.10.252.139", "3128", "广东省肇庆市");
        ProxyPo p20 = new ProxyPo("20", "60.250.81.118", "8080", "台湾");
        ProxyPo p21 = new ProxyPo("21", "113.57.252.107", "80", "武汉");
        ProxyPo p22 = new ProxyPo("22", "113.214.13.1", "8000", "浙江省杭州市 华数传媒");
        ProxyPo p23 = new ProxyPo("23", "115.29.247.115", "8888", "北京市 万网IDC机房");
        ProxyPo p24 = new ProxyPo("24", "202.106.169.228", "8080", "北京");
        ProxyPo p25 = new ProxyPo("25", "122.96.59.106", "81", "南京");
        ProxyPo p26 = new ProxyPo("26", "182.92.77.169", "3128",
                "浙江省杭州市 阿里巴巴网络有限公司");
        ProxyPo p27 = new ProxyPo("27", "113.214.13.1", "8000", "浙江省杭州市 华数传媒");
        ProxyPo p28 = new ProxyPo("28", "122.96.59.106", "81", "南京");
        ProxyPo p29 = new ProxyPo("29", "117.21.192.9", "80", "江西省 电信");
        ProxyPo p30 = new ProxyPo("30", "113.57.230.49", "81", "湖北省武汉市 联通");
        ProxyPo p31 = new ProxyPo("31", "223.68.6.10", "8000", "江苏省宿迁市 移动");
        ProxyPo p32 = new ProxyPo("32", "115.28.23.36", "3128", "北京");
        ProxyPo p33 = new ProxyPo("33", "122.96.59.106", "81", "江苏省南京市 联通");
        ProxyPo p34 = new ProxyPo("34", "202.108.23.247", "80", "北京");
        ProxyPo p35 = new ProxyPo("35", "124.207.175.91", "8080", "北京");
        ProxyPo p36 = new ProxyPo("36", "120.192.200.72", "80", "西安");
        ProxyPo p37 = new ProxyPo("37", "120.237.91.242", "3128", "北京");
        ProxyPo p38 = new ProxyPo("38", "125.39.66.76", "80", "北京");
        List<ProxyPo> list = new ArrayList();

        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        list.add(p7);
        list.add(p8);
        list.add(p9);
        list.add(p10);
        list.add(p11);
        list.add(p11);
        list.add(p12);
        list.add(p13);
        list.add(p14);
        list.add(p15);
        list.add(p16);
        list.add(p17);
        list.add(p18);
        list.add(p19);
        list.add(p20);
        list.add(p21);
        list.add(p22);
        list.add(p23);
        list.add(p24);
        list.add(p25);
        list.add(p26);
        list.add(p27);
        list.add(p28);
        list.add(p29);
        list.add(p30);
        list.add(p31);
        list.add(p32);
        list.add(p33);
        list.add(p34);
        list.add(p35);
        list.add(p36);
        list.add(p37);
        list.add(p38);
        int i = Tools.toRodom(38, 1);
        System.getProperties().setProperty("http.proxyHost",
                list.get(i).getIp());
        System.getProperties().setProperty("http.proxyPort",
                list.get(i).getPort());
        System.out.println("代理服务器IP::" + list.get(i).getIp() + "端口：："
                + list.get(i).getPort());
        // 确定代理是否设置成功

    }

    public static ProxyIpBean getProxyIp() {
        // String str="";
        ProxyIpBean ipBean = new ProxyIpBean();
        System.setProperty("http.maxRedirects", "50");
        System.getProperties().setProperty("proxySet", "true");

        // 如果不设置，只要代理IP和代理端口正确,此项不设置也可以
        ProxyPo p1 = new ProxyPo("1", "41.231.53.41", "3128", "突尼斯");
        ProxyPo p2 = new ProxyPo("2", "114.112.91.135", "3128", "北京市");
        ProxyPo p3 = new ProxyPo("3", "111.161.126.83", "8080", "天津市 联通");
        ProxyPo p4 = new ProxyPo("4", "111.161.126.84", "80", "天津市 联通");
        ProxyPo p5 = new ProxyPo("5", "111.161.126.89", "8080", "天津市 联通");
        ProxyPo p6 = new ProxyPo("6", "111.161.126.85", "80", "天津市 ");
        ProxyPo p7 = new ProxyPo("7", "111.161.126.92", "8080", "突尼斯");
        ProxyPo p8 = new ProxyPo("8", "183.224.1.30", "80", "昆明");
        ProxyPo p9 = new ProxyPo("9", "111.161.126.88", "8080", "天津");
        ProxyPo p10 = new ProxyPo("10", "14.18.16.67", "80", "广州");
        ProxyPo p11 = new ProxyPo("11", "222.246.232.55", "80", "湖南");
        ProxyPo p12 = new ProxyPo("12", "220.181.32.106", "80", "北京");
        ProxyPo p13 = new ProxyPo("13", "202.108.23.247", "80", "北京");

        ProxyPo p14 = new ProxyPo("14", "106.3.40.249", "8081", "北京");
        ProxyPo p15 = new ProxyPo("15", "58.56.124.192", "80", "济南");
        ProxyPo p16 = new ProxyPo("16", "223.202.3.49", "8080", "北京");
        ProxyPo p17 = new ProxyPo("17", "218.4.236.117", "80", "江苏");
        ProxyPo p18 = new ProxyPo("18", "120.210.202.4", "80", "安徽");
        ProxyPo p19 = new ProxyPo("19", "121.10.252.139", "3128", "广东省肇庆市");
        ProxyPo p20 = new ProxyPo("20", "60.250.81.118", "8080", "台湾");
        ProxyPo p21 = new ProxyPo("21", "113.57.252.107", "80", "武汉");
        ProxyPo p22 = new ProxyPo("22", "113.214.13.1", "8000", "浙江省杭州市 华数传媒");
        ProxyPo p23 = new ProxyPo("23", "115.29.247.115", "8888", "北京市 万网IDC机房");
        ProxyPo p24 = new ProxyPo("24", "202.106.169.228", "8080", "北京");
        ProxyPo p25 = new ProxyPo("25", "122.96.59.106", "81", "南京");
        ProxyPo p26 = new ProxyPo("26", "182.92.77.169", "3128",
                "浙江省杭州市 阿里巴巴网络有限公司");
        ProxyPo p27 = new ProxyPo("27", "113.214.13.1", "8000", "浙江省杭州市 华数传媒");
        ProxyPo p28 = new ProxyPo("28", "122.96.59.106", "81", "南京");
        ProxyPo p29 = new ProxyPo("29", "117.21.192.9", "80", "江西省 电信");
        ProxyPo p30 = new ProxyPo("30", "113.57.230.49", "81", "湖北省武汉市 联通");
        ProxyPo p31 = new ProxyPo("31", "223.68.6.10", "8000", "江苏省宿迁市 移动");
        ProxyPo p32 = new ProxyPo("32", "115.28.23.36", "3128", "北京");
        ProxyPo p33 = new ProxyPo("33", "122.96.59.106", "81", "江苏省南京市 联通");
        ProxyPo p34 = new ProxyPo("34", "202.108.23.247", "80", "北京");
        ProxyPo p35 = new ProxyPo("35", "124.207.175.91", "8080", "北京");
        ProxyPo p36 = new ProxyPo("36", "120.192.200.72", "80", "西安");
        ProxyPo p37 = new ProxyPo("37", "120.237.91.242", "3128", "北京");
        ProxyPo p38 = new ProxyPo("38", "125.39.66.76", "80", "北京");
        List<ProxyPo> list = new ArrayList();

        list.add(p1);
        list.add(p2);
        list.add(p3);
        list.add(p4);
        list.add(p5);
        list.add(p6);
        list.add(p7);
        list.add(p7);
        list.add(p8);
        list.add(p9);
        list.add(p10);
        list.add(p11);
        list.add(p11);
        list.add(p12);
        list.add(p13);
        list.add(p14);
        list.add(p15);
        list.add(p16);
        list.add(p17);
        list.add(p18);
        list.add(p19);
        list.add(p20);
        list.add(p21);
        list.add(p22);
        list.add(p23);
        list.add(p24);
        list.add(p25);
        list.add(p26);
        list.add(p27);
        list.add(p28);
        list.add(p29);
        list.add(p30);
        list.add(p31);
        list.add(p32);
        list.add(p33);
        list.add(p34);
        list.add(p35);
        list.add(p36);
        list.add(p37);
        list.add(p38);
        int i = Tools.toRodom(38, 1);

        ipBean.setIp(list.get(i).getIp());
        ipBean.setPort(Integer.valueOf(list.get(i).getPort()));

        //System.getProperties().setProperty("http.proxyHost",list.get(i).getIp());
        //System.getProperties().setProperty("http.proxyPort",list.get(i).getPort());
        System.out.println("代理服务器IP::" + list.get(i).getIp() + "端口：："
                + list.get(i).getPort());
        // 确定代理是否设置成功
        return ipBean;
    }
    public static Document getHtml(String url1) throws Exception {
        // TODO Auto-generated method stub
        Document doc = Jsoup.connect(url1).get();
        return doc;
    }

    private static Document getHtmlStr(String address) throws Exception,
            RuntimeException {
        StringBuffer html = new StringBuffer();
        String result = null;

        URL url = new URL(address);

        URLConnection conn = (URLConnection) url.openConnection();
        conn.setConnectTimeout(1000 * 40);
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0");
        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());

        String inputLine;
        byte[] buf = new byte[4096];
        int bytesRead = 0;
        while (bytesRead >= 0) {
            inputLine = new String(buf, 0, bytesRead, "ISO-8859-1");
            html.append(inputLine);
            bytesRead = in.read(buf);
            inputLine = null;
        }
        result = new String(html.toString().trim().getBytes("ISO-8859-1"),
                "utf-8").toLowerCase();
        buf = null;
        Document doc = Jsoup
                .parse(result, "", new Parser(new XmlTreeBuilder()));
        // System.out.println(result);
        return doc;
    }

    public static void main(String[] args) throws Exception {
        // Document htmlStr =
        // ProxyIp.getHtmlStr("http://hehongwei44.iteye.com/blog/1494999");
        // System.out.println("==="+htmlStr);
        ProxyIp.setProxy();
        System.out.println("==============");
    }
}

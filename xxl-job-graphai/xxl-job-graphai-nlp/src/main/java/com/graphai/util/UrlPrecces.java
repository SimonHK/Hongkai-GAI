package com.graphai.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlPrecces {

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     * @author lzf
     */
    private static String TruncateUrlPage(String strURL){
        String strAllParam=null;
        String[] arrSplit=null;
        strURL=strURL.trim().toLowerCase();
        arrSplit=strURL.split("[?]");
        if(strURL.length()>1){
            if(arrSplit.length>1){
                for (int i=1;i<arrSplit.length;i++){
                    strAllParam = arrSplit[i];
                }
            }
        }
        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     * @author lzf
     */
    public static Map<String, String> urlSplit(String URL){
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit=null;
        String strUrlParam=TruncateUrlPage(URL);
        if(strUrlParam==null){
            return mapRequest;
        }
        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit){
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");
            //解析出键值
            if(arrSplitEqual.length>1){
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }else{
                if(arrSplitEqual[0]!=""){
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    /**
     * 获取网站IP地址
     *
     * */
    public static String getIPSimple(String url) {
        //使用正则表达式过滤，
        String re = "((http|ftp|https)://)(([a-zA-Z0-9._-]+)|([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}))(([a-zA-Z]{2,6})|(:[0-9]{1,4})?)";
        String str = "";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(re);
        // 忽略大小写的写法
        // Pattern pat = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        //若url==http://127.0.0.1:9040或www.baidu.com的，正则表达式表示匹配
        if (matcher.matches()) {
            str = url;
        } else {
            String[] split2 = url.split(re);
            if (split2.length > 1) {
                String substring = url.substring(0, url.length() - split2[1].length());
                str = substring;
            } else {
                str = split2[0];
            }
        }
        return str;
    }

    private static URI getIP(URI uri) {
        URI effectiveURI = null;

        try {
            // URI(String scheme, String userInfo, String host, int port, String
            // path, String query,String fragment)
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
        } catch (Throwable var4) {
            effectiveURI = null;
        }

        return effectiveURI;
    }


    public static String gotIp(String url){
        URI ip = getIP(URI.create(url));
        return ip.getHost();
    }


    public static void main (String[] args){
        /*http://www.sohu.com/
    http://finance.eastmoney.com/
    https://www.zhitongcaijing.com
    http://www.10jqka.com.cn/
    http://www.jrj.com.cn/
    http://www.hexun.com/
    http://www.ifeng.com/
    http://finance.sina.com.cn/
    http://www.cnfol.com/
    http://www.xinhuanet.com/
    http://www.people.com.cn/
    https://finance.qq.com/


    http://zdb.pedaily.cn/inv/
    https://www.pedata.cn
    http://www.cyzone.cn/
    http://www.newseed.cn/invest
    https://www.itjuzi.com/
    https://www.jianyu360.com/jylab/supsearch/index.html

    http://www.court.gov.cn/zgcpwsw/
    http://susong.chinacourt.org/
    http://www.315.gov.cn/
    http://www.faxin.cn/

    http://www.gdcenn.cn/

    https://www.creditchina.gov.cn/
    http://zhixing.court.gov.cn/search/
    http://www.gsxt.gov.cn/corp-query-entprise-info-xxgg-100000.html
    http://hd.chinatax.gov.cn/xxk/*/
    System.out.print(gotIp("https://www.jianyu360.com/jylab/supsearch/index.html"));

    }
}

package com.graphai.drivers.Proxy;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ProxyConfig;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class TestDynamicIp {
	public static List<String> ipList = new ArrayList<String>();
	public static boolean gameOver = false;
	public static void main(String[] args) {
		long fetchIpSeconds = 5;
		int threadNum = 10;
		int testTime = 3;
		// 请填写全网代理IP订单号，填写之后才可以提取到IP哦
		String order = "一定要把这里改为单号哦~";
		// 你要抓去的目标网址
		String targetUrl = "http://1212.ip138.com/ic.asp";
		// 是否加载JS，加载JS会导致速度变慢
		boolean useJS = false;
		// 请求超时时间，单位毫秒，默认5秒
		int timeOut = 5000;
		
		if (order == null || "".equals(order)) {
			System.err.println("请输入全网代理IP动态代理订单号");
			return;
		}
		
		System.out.println(">>>>>>>>>>>>>>全网代理动态IP测试开始<<<<<<<<<<<<<<");
		System.out.println("***************");
		System.out.println("接口返回IP为国内各地区，每次最多返回10个");
		System.out.println("提取IP间隔 " + fetchIpSeconds + " 秒 ");
		System.out.println("开启爬虫线程 " + threadNum);
		System.out.println("爬虫目标网址  " + targetUrl);
		System.out.println("测试次数 3 ");
		System.out.println("***************\n");
		TestDynamicIp tester = new TestDynamicIp();
		new Thread(tester.new GetIP(fetchIpSeconds * 1000, testTime, order)).start();
		for (int i = 0; i < threadNum; i++) {
			tester.new Crawler(100, targetUrl, useJS, timeOut).start();
		}
		while(!gameOver){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println(">>>>>>>>>>>>>>全网代理动态IP测试结束<<<<<<<<<<<<<<");
		System.exit(0);
	}
    
	// 抓取IP138，检测IP
	public class Crawler extends Thread{
		@Override
		public void run() {
			while(!gameOver){
				webParseHtml(targetUrl);
				try {
					Thread.sleep(sleepMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		long sleepMs = 200;
		boolean useJs = false;
		String targetUrl = "";
		int timeOut = 5000;
		
		public Crawler(long sleepMs, String targetUrl, boolean useJs, int timeOut) {
			this.sleepMs = sleepMs;
			this.targetUrl = targetUrl;
			this.useJs = useJs;
			this.timeOut = timeOut;
		}
		public String webParseHtml(String url) {
			String html = "";
			BrowserVersion[] versions = {BrowserVersion.EDGE, BrowserVersion.CHROME, BrowserVersion.FIREFOX_45, BrowserVersion.INTERNET_EXPLORER};
			WebClient client = new WebClient(versions[(int)(versions.length * Math.random())]);
			try {
				client.getOptions().setThrowExceptionOnFailingStatusCode(false);
				client.getOptions().setJavaScriptEnabled(useJs);
				client.getOptions().setCssEnabled(false);
				client.getOptions().setThrowExceptionOnScriptError(false);
				client.getOptions().setTimeout(timeOut);
				client.getOptions().setAppletEnabled(true);
				client.getOptions().setGeolocationEnabled(true);
				client.getOptions().setRedirectEnabled(true);
				
				String ipport = getAProxy();
				if (ipport != null) {
					ProxyConfig proxyConfig = new ProxyConfig(ipport.split(":")[0], Integer.parseInt(ipport.split(":")[1]));
					client.getOptions().setProxyConfig(proxyConfig);
				}else {
					System.out.print(".");
					return "";
				}
			
				HtmlPage page = client.getPage(url);
				html = page.asXml();
				
				System.out.println(getName() + " 使用代理 " + ipport + "请求目标网址返回HTML：" + html);
				
			} catch (Exception e) {
				return webParseHtml(url);
			} finally {
				client.close();
			}
			return html;
		}
		
	    private String getAProxy() {
	    	if (ipList.size() > 0) {
	    		String ip = ipList.get((int)(Math.random() * ipList.size()));
	    		return ip ;
			}
			return null;
		}
	}
	
	// 定时获取动态IP
	public class GetIP implements Runnable{
		long sleepMs = 1000;
		int maxTime = 3;
		String order = "";
		
		public GetIP(long sleepMs, int maxTime, String order) {
			this.sleepMs = sleepMs;
			this.maxTime = maxTime;
			this.order = order;
		}
		
		@Override
		public void run() {
			long getIpTime = 0;
			int time = 1;
			while(!gameOver){
				if(time >= 4){
					gameOver = true;
					break;
				}
				try {
					java.net.URL url = new java.net.URL("http://dynamic.goubanjia.com/dynamic/get/" + order + ".html?ttl");
			    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			    	connection.setConnectTimeout(3000);
			    	connection = (HttpURLConnection)url.openConnection();
			    	
			        InputStream raw = connection.getInputStream();  
			        InputStream in = new BufferedInputStream(raw);  
			        byte[] data = new byte[in.available()];
			        int bytesRead = 0;  
			        int offset = 0;  
			        while(offset < data.length) {  
			            bytesRead = in.read(data, offset, data.length - offset);  
			            if(bytesRead == -1) {  
			                break;  
			            }  
			            offset += bytesRead;  
			        }  
			        in.close();  
			        raw.close();
					String[] res = new String(data, "UTF-8").split("\n");
					List<String> ipList = new ArrayList<String>();
					for (String ip : res) {
						try {
							String[] parts = ip.split(",");
							if (Integer.parseInt(parts[1]) > 0) {
								ipList.add(parts[0]);
							}
						} catch (Exception e) {
						}
					}
					if (ipList.size() > 0) {
						TestDynamicIp.ipList = ipList;
						System.out.println("第" + ++getIpTime + "次获取动态IP " + ipList.size() + " 个");
						time += 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println(">>>>>>>>>>>>>>获取IP出错");
				}
				try {
					Thread.sleep(sleepMs);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}

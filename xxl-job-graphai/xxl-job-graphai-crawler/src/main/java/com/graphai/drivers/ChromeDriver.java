package com.graphai.drivers;


import com.graphai.drivers.Proxy.ProxyIp;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;


/**
 * chromeDriver是谷歌的浏览器驱动，用来适配Selenium,有图形页面存在，在调试爬虫下载运行的功能的时候会相对方便
 * @author zhuangj
 * @date 2017/11/14
 */
public class ChromeDriver {

    private static ChromeDriverService service;

    public static WebDriver getChromeDriver() throws IOException {
        System.setProperty("webdriver.chrome.driver","/Users/hongkai/Development/GitHubSource/GraphAI/Hongkai-GAI/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/mac/chrome/driver/chromedriver");
        // 创建一个 ChromeDriver 的接口，用于连接 Chrome（chromedriver.exe 的路径可以任意放置，只要在newFile（）的时候写入你放的路径即可）
        System.getProperties().setProperty("proxySet", "true");
        //String ip = "93.91.200.146";
        //System.getProperties().setProperty("http.proxyHost", ip);
        //System.getProperties().setProperty("http.proxyPort", "80");
        ProxyIp.setProxy();
        service = new ChromeDriverService.Builder().usingDriverExecutable(new File("/Users/hongkai/Development/GitHubSource/GraphAI/Hongkai-GAI/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/mac/chrome/driver/chromedriver")) .usingAnyFreePort().build();
        service.start();
        // 创建一个 Chrome 的浏览器实例
        return new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
    }

    public static void main(String[] args) throws IOException {

        WebDriver driver = ChromeDriver.getChromeDriver();
        // 让浏览器访问 Baidu
        driver.get("http://wenshu.court.gov.cn/list/list/?sorttype=1&number=YGXC7GMA&guid=c1264777-8751-9f272758-bf1ed1e75305&conditions=searchWord+2+AJLX++%E6%A1%88%E4%BB%B6%E7%B1%BB%E5%9E%8B:%E6%B0%91%E4%BA%8B%E6%A1%88%E4%BB%B6&conditions=searchWord+%E5%90%88%E5%90%8C+++%E5%85%B3%E9%94%AE%E8%AF%8D:%E5%90%88%E5%90%8C");
        // 用下面代码也可以实现
        //driver.navigate().to("http://www.baidu.com");
        // 获取 网页的 title
        System.out.println(" Page title is: " +driver.getTitle());
        System.out.println(" Page getCurrentUrl is: " +driver.getCurrentUrl());
        System.out.println(" Page getPageSource is: " +driver.getPageSource());
        //System.out.println(" Page getPageSource is: " +driver.getWindowHandle());
        //System.out.println(" Page getPageSource is: " +driver.getWindowHandles());
        // 通过 id 找到 input 的 DOM
        driver.findElement(By.xpath("//*[@id=\"dataItem1\"]/table/tbody/tr[1]/td/div/a")).click();
        // 输入关键字
        //element.sendKeys("东鹏瓷砖");
        // 提交 input 所在的 form
        //element.submit();
        // 通过判断 title 内容等待搜索页面加载完毕，间隔秒
       /* new WebDriverWait(driver, 10).until(new ExpectedCondition() {
            @Override
            public Object apply(Object input) {
                return ((WebDriver)input).getTitle().toLowerCase().startsWith("东鹏瓷砖");
            }
        });*/
        //((RemoteWebElement) element).click();
        //element.submit();
        // 显示搜索结果页面的 title
        System.out.println(" Page title is: " +driver.getTitle());
        // 关闭浏览器
        driver.quit();
        // 关闭 ChromeDriver 接口
        service.stop();
    }



}

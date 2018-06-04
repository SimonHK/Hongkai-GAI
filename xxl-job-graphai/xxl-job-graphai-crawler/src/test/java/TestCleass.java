import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;

public class TestCleass {



    public static void main(String [] args){

            // TODO Auto-generated method stub
            //selenium系统配置，其中的路径写自己config文件的路径
            System.setProperty("selenuim_config", "/Users/hongkai/Development/GitHubSource/xxl-job/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/drivers/config.ini");
            Spider.create(new GithubRepoPageProcessor())//调用一个webmagic中封装好的一个网页爬取类
                    .addUrl("http://www.baidu.com")//要爬取的网页
                    //浏览器驱动（动态网页信息通过模拟浏览器启动获取）
                   // .setDownloader(new SeleniumDownloader("/Users/hongkai/Development/GitHubSource/xxl-job/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/drivers/mac/chromedriver")
                    .thread(3)//启动n个线程（此语句表示启动3个线程）
                    .run();//启动爬虫，会阻塞当前线程执行（及n个线程不是同时执行的）
//		。runAsync();//启动爬虫，当前线程继续执行（及n个线程同时执行）


    }

    public static void testSelenium() {
        System.getProperties().setProperty("webdriver.chrome.driver", "/Users/hongkai/Development/GitHubSource/xxl-job/xxl-job-graphai/xxl-job-graphai-crawler/src/main/resources/drivers/mac/chromedriver2.9");
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("http://huaban.com/");
        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        System.out.println(webElement.getAttribute("outerHTML"));
        webDriver.close();
    }
}

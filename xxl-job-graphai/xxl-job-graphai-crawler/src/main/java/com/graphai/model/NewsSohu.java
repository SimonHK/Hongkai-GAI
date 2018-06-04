package com.graphai.model;

import com.graphai.process.NewsSohuPipeline;
import us.codecraft.webmagic.MultiPageModel;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.MultiPagePipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import java.util.Collection;
import java.util.List;

/**
 * @author hongkai
 */
@TargetUrl("http://www.sohu.com//\\d+/\\d+/\\d+/\\w+*.html")
public class NewsSohu implements MultiPageModel {

    @ExtractByUrl("http://www\\.sohu\\.com/\\d+/\\d+/\\d+/([^_]*).*\\.html")
    private String pageKey;

    @ExtractByUrl(value = "http://www\\.sohu\\.com/\\d+/\\d+/\\d+/\\w+_(\\d+)\\.html", notNull = false)
    private String page;

    @ExtractBy(value = "//div[@class=\"ep-pages\"]//a/regex('http://www\\.sohu\\.com/\\d+/\\d+/\\d+/\\w+_(\\d+)\\.html',1)"
            , multi = true, notNull = false)
    private List<String> otherPage;

    @ExtractBy("//h1/text()")
    private String title;

    @ExtractBy("//article[@id=\"mp-editor\"]")
    private String content;

    @ExtractBy("//span[@id=\"news-time\"]")
    private String contentTime;

    @ExtractBy("//span[@data-role=\"original-link\"]")
    private String formSource;


    @Override
    public String getPageKey() {
        return pageKey;
    }

    @Override
    public Collection<String> getOtherPages() {
        return otherPage;
    }

    @Override
    public String getPage() {
        if (page == null) {
            return "1";
        }
        return page;
    }

    @Override
    public MultiPageModel combine(MultiPageModel multiPageModel) {
        NewsSohu newsSohu = new NewsSohu();
        newsSohu.title = this.title;
        NewsSohu pagedModel1 = (NewsSohu) multiPageModel;
        newsSohu.content = this.content + pagedModel1.content;
        newsSohu.contentTime = this.contentTime;
        newsSohu.formSource = this.formSource;
        return newsSohu;
    }

    @Override
    public String toString() {
        return "NewsSohu{" +
                "content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", contentTime='"+contentTime+'\''+
                ", formSource='"+formSource+'\''+
                ", otherPage=" + otherPage +
                '}';
    }

    public static void main(String[] args) {
        OOSpider.create(Site.me(), NewsSohu.class).addUrl("http://www.sohu.com")
                .scheduler(new RedisScheduler("localhost")).addPipeline(new MultiPagePipeline()).addPipeline(new NewsSohuPipeline()).run();
    }

}

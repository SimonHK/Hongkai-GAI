package com.graphai.dao;

import com.graphai.model.Crawlercontent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CrawlercontentDao {


    public List<Crawlercontent> pageList();

    public int save(Crawlercontent ienvironment);

    public void insertBatch(List<Crawlercontent> ienvironments) throws Exception;

    public List<Crawlercontent> findPageListByTime(@Param("nowtime") String nowtime);

}

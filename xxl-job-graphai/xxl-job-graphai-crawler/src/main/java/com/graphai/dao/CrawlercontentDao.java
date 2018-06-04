package com.graphai.dao;

import com.graphai.pojo.Crawlercontent;

import java.util.List;

public interface CrawlercontentDao {


    public List<Crawlercontent> pageList();

    public int save(Crawlercontent ienvironment);

    public void insertBatch(List<Crawlercontent> ienvironments) throws Exception;
}

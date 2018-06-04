package com.graphai.dao;

import com.graphai.model.Eventlibrarynews;

import java.util.List;

public interface EventlibrarynewsDao {

    public List<Eventlibrarynews> pageList();

    public int save(Eventlibrarynews eventlibrarynews);

    public void insertBatch(List<Eventlibrarynews> eventlibrarynews) throws Exception;
}

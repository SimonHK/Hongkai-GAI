package com.graphai.dao;

import com.graphai.pojo.Tempurls;

import java.util.List;

public interface TempUrlsDao {

    public List<Tempurls> pageList();

    public int save(Tempurls tempurls);
}

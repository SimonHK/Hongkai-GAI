package com.graphai.dao;

import com.graphai.model.Storeresult;

import java.util.List;

public interface StoreresoultDao {

    public List<Storeresult> pageList();

    public int save(Storeresult storeresult);

    public void insertBatch(List<Storeresult> storeresults) throws Exception;
}

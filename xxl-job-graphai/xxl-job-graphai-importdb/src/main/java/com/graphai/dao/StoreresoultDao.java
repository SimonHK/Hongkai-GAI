package com.graphai.dao;

import com.graphai.model.Storeresult;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StoreresoultDao {

    public List<Storeresult> pageList();

    public int save(Storeresult storeresult);

    public void insertBatch(List<Storeresult> storeresults) throws Exception;

    public List<Storeresult> findPageListByTime(@Param("indbtime") String indbtime);
}

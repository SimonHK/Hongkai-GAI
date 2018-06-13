package com.graphai.dao;

import com.graphai.model.Eventlibrarynews;
import com.graphai.model.Locations;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface LocationsDao {

    public List<Locations> pageList();

    public int save(Locations locations);

    public void insertBatch(List<Locations> locations) throws Exception;

    public int locationCount();

    public List<Locations> getlocationCountByDymanic(@Param("stratRow") int stratRow,@Param("endRow") int endRow);
}

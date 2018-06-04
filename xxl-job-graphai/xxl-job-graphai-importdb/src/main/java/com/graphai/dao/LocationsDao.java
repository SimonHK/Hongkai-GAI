package com.graphai.dao;

import com.graphai.model.Eventlibrarynews;
import com.graphai.model.Locations;

import java.util.List;

public interface LocationsDao {

    public List<Locations> pageList();

    public int save(Locations locations);

    public void insertBatch(List<Locations> locations) throws Exception;
}

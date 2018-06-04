package com.graphai.dao;

import com.graphai.model.Ienvironment;

import java.util.List;

public interface IenvironmentDao {

    public List<Ienvironment> pageList();

    public int save(Ienvironment ienvironment);

    public void insertBatch(List<Ienvironment> ienvironments) throws Exception;
}

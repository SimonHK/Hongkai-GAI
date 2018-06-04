package com.graphai.dao;

import com.graphai.model.TestInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TestDao {
    public List<TestInfo> pageList();
}

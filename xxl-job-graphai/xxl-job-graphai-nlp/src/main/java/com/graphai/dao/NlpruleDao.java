package com.graphai.dao;

import com.graphai.model.Nlprule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NlpruleDao {

    public List<Nlprule> pageList();

    public List<Nlprule> pageListForRuleName(@Param("urlName") String parm);
}

package com.dhk.crm.workbench.dao;

import com.dhk.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    Tran detail(String id);

    Integer getTotalByCondition(Map<String, Object> map);

    List<Tran> pageList(Map<String, Object> map);

    int changeStage(Tran tran);

    int getTotal();

    List<Map<String, Object>> getCharts();

}

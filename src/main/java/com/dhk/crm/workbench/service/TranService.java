package com.dhk.crm.workbench.service;

import com.dhk.crm.workbench.domain.Tran;
import com.dhk.crm.workbench.domain.TranHistory;

import java.util.List;
import java.util.Map;

public interface TranService {
    boolean save(Tran tran, String customerName);

    Tran detail(String id);

    Map<String, Object> pageList(Map<String, Object> map);

    List<TranHistory> getHistoryListByTranId(String tranId);

    boolean changeStage(Tran tran);

    Map<String, Object> getCharts();
}

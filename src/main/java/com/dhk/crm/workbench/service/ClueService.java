package com.dhk.crm.workbench.service;

import com.dhk.crm.workbench.domain.Clue;
import com.dhk.crm.workbench.domain.Tran;

import java.util.Map;

public interface ClueService {
    boolean save(Clue clue);

    Map<String, Object> pageList(Map<String, Object> map);

    Clue detail(String id);

    boolean unbund(String id);

    boolean bund(String clueId, String[] activityId);

    boolean convert(String clueId, Tran t, String createBy);
}

package com.dhk.crm.settings.dao;

import com.dhk.crm.settings.domain.DicValue;

import java.util.List;

public interface DicValueDao {
    List<DicValue> getListByCode(String code);
}

package com.dhk.crm.workbench.dao;

import com.dhk.crm.workbench.domain.Contacts;

import java.util.List;

public interface ContactsDao {

    int save(Contacts contacts);

    List<Contacts> getActivityListByName(String cname);
}

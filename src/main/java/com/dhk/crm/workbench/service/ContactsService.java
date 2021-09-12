package com.dhk.crm.workbench.service;

import com.dhk.crm.workbench.domain.Contacts;

import java.util.List;

public interface ContactsService {
    List<Contacts> getActivityListByName(String cname);
}

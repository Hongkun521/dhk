package com.dhk.crm.workbench.service.impl;

import com.dhk.crm.workbench.dao.ContactsDao;
import com.dhk.crm.workbench.domain.Contacts;
import com.dhk.crm.workbench.service.ContactsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContactsServiceImpl implements ContactsService {
    @Resource
    private ContactsDao contactsDao;

    @Override
    public List<Contacts> getActivityListByName(String cname) {
        List<Contacts> cList=contactsDao.getActivityListByName(cname);
        return cList;
    }
}

package com.dhk.crm.settings.service;

import com.dhk.crm.exception.LoginException;
import com.dhk.crm.settings.domain.User;

import java.util.List;

public interface UserService {
    User login(String loginAct, String loginPwd, String ip) throws LoginException;
    List<User> getUserList();
}

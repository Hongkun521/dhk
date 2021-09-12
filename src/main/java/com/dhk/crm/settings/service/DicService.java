package com.dhk.crm.settings.service;

import com.dhk.crm.settings.domain.DicValue;

import javax.servlet.ServletContext;
import java.util.List;
import java.util.Map;

public interface DicService {
    Map<String, List<DicValue>> getAll(ServletContext application);
}

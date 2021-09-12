package com.dhk.crm.settings.service.impl;

import com.dhk.crm.settings.dao.DicTypeDao;
import com.dhk.crm.settings.dao.DicValueDao;
import com.dhk.crm.settings.domain.DicType;
import com.dhk.crm.settings.domain.DicValue;
import com.dhk.crm.settings.service.DicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DicServiceImpl implements DicService {
    @Resource
    private DicTypeDao dicTypeDao;
    @Resource
    private DicValueDao dicValueDao;

    @Override
    public Map<String, List<DicValue>> getAll(ServletContext application) {
        Map<String, List<DicValue>> map=new HashMap<>();

        //将字典类型数据取出
        List<DicType> dtList=dicTypeDao.getTypeList();

        //将字典类型遍历
        for(DicType dt:dtList){

            //取得每一种类型的字典类型编码
            String code = dt.getCode();
            //根据每一个字典类型取得字典值列表
            List<DicValue> dvList=dicValueDao.getListByCode(code);
            map.put(code+"List",dvList);
        }


        return map;

    }
}

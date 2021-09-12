package com.dhk.crm.workbench.service.impl;

import com.dhk.crm.settings.dao.UserDao;
import com.dhk.crm.settings.domain.User;
import com.dhk.crm.vo.PaginationVO;
import com.dhk.crm.workbench.dao.ActivityDao;
import com.dhk.crm.workbench.dao.ActivityRemarkDao;
import com.dhk.crm.workbench.domain.Activity;
import com.dhk.crm.workbench.domain.ActivityRemark;
import com.dhk.crm.workbench.service.ActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Resource
    private ActivityDao activityDao;
    @Resource
    private ActivityRemarkDao activityRemarkDao;
    @Resource
    private UserDao userDao;

    @Override
    public boolean save(Activity activity) {
        boolean flag=true;
        int count=activityDao.save(activity);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public PaginationVO<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total=activityDao.getTotalByCondition(map);
        //取得dataList
        List<Activity> dataList=activityDao.getActivityListByCondition(map);
        //创建一个vo对象将totalList和dataList封装到vo中
        PaginationVO<Activity> vo=new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    @Override
    public boolean delete(String[] ids) {
        boolean flag=true;
        //查询需要删除的备注的数量
        int count1=activityRemarkDao.getCountByAids(ids);
        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2=activityRemarkDao.deleteByAids(ids);
        if(count1!=count2){
            flag=false;
        }
        //备注全部删掉了就会删掉市场活动的表
        //删除市场活动
        int count3=activityDao.delete(ids);
        if(ids.length != count3){
            flag=false;
        }
        return flag;

    }

    @Override
    public Map<String, Object> getUserListAndActivity(String id) {
        //去uList
        List<User> userList=userDao.getUserList();
        //取a
        Activity a=activityDao.getById(id);
        //将uList和a打包到map中
        Map<String,Object> map=new HashMap<>();
        map.put("uList",userList);
        map.put("a",a);
        //返回map
        return map;
    }

    @Override
    public boolean update(Activity activity) {
        boolean flag=true;
        int count=activityDao.update(activity);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        Activity activity=activityDao.detail(id);
        return activity;
    }

    @Override
    public List<ActivityRemark> getRemarkListById(String activityId) {
        List<ActivityRemark> activityList=activityRemarkDao.getRemarkListById(activityId);
        return activityList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag=true;

        int count=activityRemarkDao.deleteById(id);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag=true;

        int count=activityRemarkDao.saveRemark(ar);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public boolean updateRemark(ActivityRemark ar) {
        boolean flag=true;

        int count=activityRemarkDao.updateRemark(ar);
        if(count!=1){
            flag=false;
        }
        return flag;
    }

    @Override
    public List<Activity> getActivityListByClueId(String cludId) {
        List<Activity> aList=activityDao.getActivityListByClueId(cludId);

        return aList;
    }

    @Override
    public List<Activity> getActivityListByNameAndNotByClueId(Map<String, String> map) {
        List<Activity> aList=activityDao.getActivityListByNameAndNotByClueId(map);
        return aList;
    }

    @Override
    public List<Activity> getActivityListByName(String aname) {
        List<Activity> aList=activityDao.getActivityListByName(aname);
        return aList;
    }
}

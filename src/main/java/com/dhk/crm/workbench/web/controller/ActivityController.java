package com.dhk.crm.workbench.web.controller;

import com.dhk.crm.exception.MyUserException;
import com.dhk.crm.settings.domain.User;
import com.dhk.crm.settings.service.UserService;
import com.dhk.crm.utils.DateTimeUtil;
import com.dhk.crm.utils.UUIDUtil;
import com.dhk.crm.vo.PaginationVO;
import com.dhk.crm.workbench.domain.Activity;
import com.dhk.crm.workbench.domain.ActivityRemark;
import com.dhk.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

@Controller
@RequestMapping(value="/workbench/activity")
public class ActivityController {
    @Resource
    private ActivityService activityService;
    @Resource
    private UserService userService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;

   //处理器方法返回void， 响应ajax请求

    @RequestMapping("/updateRemark.do")
    @ResponseBody
    private Map<String,Object> updateRemark(HttpSession session,ActivityRemark ar){
        System.out.println("执行修改备注的操作");

        String editTime=DateTimeUtil.getSysTime();
        String editBy=((User)session.getAttribute("user")).getName();
        String editFlag="1";

        ar.setEditTime(editTime);
        ar.setEditBy(editBy);
        ar.setEditFlag(editFlag);

        boolean flag=activityService.updateRemark(ar);

        Map<String,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);

        return map;
    }

    @RequestMapping("/saveRemark.do")
    @ResponseBody
    private Map<String,Object> saveRemark(HttpSession session, ActivityRemark ar){
        System.out.println("执行添加备注操作");

        String id=UUIDUtil.getUUID();
        String createTime=DateTimeUtil.getSysTime();
        String createBy=((User)session.getAttribute("user")).getName();
        String editFlag="0";

        ar.setId(id);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);

        boolean flag=activityService.saveRemark(ar);

        /**
         * data
         *      {"success":true/false,"ar":{备注}}
         */
        Map<String,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("ar",ar);

        return map;
    }


   @RequestMapping("/deleteRemark.do")
   @ResponseBody
   private boolean deleteRemark(String id){
       System.out.println("执行删除备注操作");
       boolean flag=activityService.deleteRemark(id);
       return flag;
   }

    @RequestMapping("/showRemarkListByAid.do")
    @ResponseBody
    private List<ActivityRemark> showRemarkListById(String activityId){
        System.out.println("根据市场活动id，取得备注信息列表");
        List<ActivityRemark> remarkList=activityService.getRemarkListById(activityId);

        return remarkList;
    }


    @RequestMapping("/detail.do")
    private ModelAndView detail(String id){
        System.out.println("进入到跳转详细信息页的操作");
        Activity activity=activityService.detail(id);
        ModelAndView mv=new ModelAndView();
        mv.addObject(activity);
        mv.setViewName("forward:/workbench/activity/detail.jsp");
        return mv;
    }

    @RequestMapping("/update.do")
    @ResponseBody
    private boolean update(Activity activity,HttpServletRequest request){
        System.out.println("执行市场活动修改操作");

        //修改时间:当前系统时间
        String editTime= DateTimeUtil.getSysTime();
        //当前登陆的用户
        String editBy=((User)request.getSession().getAttribute("user")).getName();

        //使用springMVC用对象接受参数
        activity.setEditTime(editTime);
        activity.setEditBy(editBy);

        boolean flag=activityService.update(activity);
        return flag;
    }

    @RequestMapping("/getUserListAndActivity.do")
    @ResponseBody
    private Map<String,Object> getUserListAndActivity(String id){
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单条记录的操作");
        /*
          总结：controller调用service的方法，返回值应该是什么，你得想一想前端要的是什么，我们就要从service层取什么

          前端需要的，管业务层去要

          前端要 uList   a

          以上两项信息，复用率不高，所以选择使用map打包这两项信息即可
         */
        Map<String,Object> map=activityService.getUserListAndActivity(id);
        return map;
    }


    @RequestMapping("/delete.do")
    @ResponseBody
    private boolean delete(@RequestParam(value = "id") String[] ids){
        System.out.println("执行市场获得的删除操作");
        boolean flag = activityService.delete(ids);
        return flag;
    }


    @RequestMapping(value = "/pageList.do")
    @ResponseBody
    private PaginationVO<Activity> pageList(Activity activity,Integer pageNo,Integer pageSize) throws MyUserException {
        System.out.println("进入到查询市场活动信息列表的操作(结合条件查询+分页查询)");
        String name = activity.getName();
        String owner = activity.getOwner();
        String startDate = activity.getStartDate();
        String endDate = activity.getEndDate();
        System.out.println("name:"+name+"owner:"+owner);

        //计算出略过的记录数

        int skipCount=(pageNo-1)*pageSize;

        Map<String,Object> map=new HashMap<>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);

        // 因为以下两条信息不在domain类中,所以选择使用map进行传值(<parameterType>传值不能使用vo类,<resultType>传值可以使用vo类)
        map.put("pageSize", pageSize);
        map.put("skipCount", skipCount);
        /**
         * 前端要: 市场活动信息列表
         *          查询的总条数
         *
         *          业务层拿到了以上两项信息之后,如何做返回
         *          map
         *          map.put("dataList",dataList);
         *          map.put("total",total);
         *          PrintJson map --> json
         *          {"total":100,"dataList":[{市场活动1},{2},{3}...]
         *
         *          vo
         *          PaginationVo<T>
         *              private int total;
         *              private List<T> dataList;
         *
         *          PaginationVo<Activity> vo = new PaginationVo<>();
         *          vo.setTotal(total);
         *          vo.setDataList(dataList);
         *          PrintJson vo --> json
         *          {"total":100,"dataList":[{市场活动1},{2},{3}...]}
         *
         *          将来分页查询: 每个模块都有,所以我们选择使用一个通用的vo,操作起来比较方便
         */
        PaginationVO<Activity> vo=activityService.pageList(map);
        return vo;

    }


    @RequestMapping(value = "/save.do")
    @ResponseBody
    private boolean save(HttpServletRequest request,Activity activity) throws MyUserException {
        System.out.println("执行市场活动添加操作");
        String id= UUIDUtil.getUUID();
        //创建时间当前系统时间
        String createTime= DateTimeUtil.getSysTime();
        //当前登陆的用户
        String createBy=((User)request.getSession().getAttribute("user")).getName();

        //使用springMVC用对象接受参数
        activity.setId(id);
        activity.setCreateTime(createTime);
        activity.setCreateBy(createBy);

        boolean flag=activityService.save(activity);
        return flag;

    }

    @RequestMapping(value="/getUserList.do")
    @ResponseBody
    private List<User> getUserList(){
        //不能为私有的方法，私有的方法前段ajax请求访问不到
        System.out.println("取得用户信息列表");
        List<User> userList = userService.getUserList();
        return userList;
    }


}

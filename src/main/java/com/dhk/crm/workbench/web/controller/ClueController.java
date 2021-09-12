package com.dhk.crm.workbench.web.controller;

import com.dhk.crm.settings.domain.User;
import com.dhk.crm.settings.service.UserService;
import com.dhk.crm.utils.DateTimeUtil;
import com.dhk.crm.utils.UUIDUtil;
import com.dhk.crm.workbench.domain.Activity;
import com.dhk.crm.workbench.domain.Clue;
import com.dhk.crm.workbench.domain.Tran;
import com.dhk.crm.workbench.service.ActivityService;
import com.dhk.crm.workbench.service.ClueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/workbench/clue")
public class ClueController {
    @Resource
    private ClueService clueService;
    @Resource
    private UserService userService;
    @Resource
    private ActivityService activityService;


    @RequestMapping("/getUserList.do")
    @ResponseBody
    private List<User> getUserList(){
        List<User> userList = userService.getUserList();
        return  userList;
    }

    @RequestMapping("/save.do")
    @ResponseBody
    private boolean save(HttpSession session,Clue clue){
        System.out.println("执行添加线索操作");

        String id= UUIDUtil.getUUID();
        String createBy=((User)session.getAttribute("user")).getName();
        String createTime= DateTimeUtil.getSysTime();

        clue.setId(id);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);

        boolean flag = clueService.save(clue);
        return  flag;
    }


    @RequestMapping("/pageList.do")
    @ResponseBody
    private Map<String,Object> pageList(Clue clue, Integer pageNo, Integer pageSize){
        System.out.println("进入到查询市场线索活动信息列表的操作(结合条件查询+分页查询)");
        String fullname = clue.getFullname();
        String company = clue.getCompany();
        String phone = clue.getPhone();
        String source = clue.getSource();
        String owner = clue.getOwner();
        String mphone = clue.getMphone();
        String state = clue.getState();

        int skipCount = (pageNo-1) * pageSize;
        Map<String,Object> map = new HashMap<>();
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);
        map.put("fullname",fullname);
        map.put("company",company);
        map.put("phone",phone);
        map.put("source",source);
        map.put("owner",owner);
        map.put("mphone",mphone);
        map.put("state",state);
        Map<String,Object> cMap = clueService.pageList(map);
        return cMap;


    }

    @RequestMapping("/detail.do")
    private ModelAndView detail(String id){
        System.out.println("跳转到线索的详细信息页，并获取id:"+id);
        Clue clue=clueService.detail(id);

        ModelAndView mv=new ModelAndView();
        mv.addObject("clue",clue);
        mv.setViewName("forward:/workbench/clue/detail.jsp");
        return  mv;
    }


    @RequestMapping("/getActivityListByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByClueId(String clueId){
        System.out.println("根据线索的id查询关联的市场活动列表");
        List<Activity> aList=activityService.getActivityListByClueId(clueId);

        return aList;
    }

    @RequestMapping("/unbund.do")
    @ResponseBody
    private boolean unbund(String id){
        System.out.println("执行解除关联操作");
        boolean flag=clueService.unbund(id);

        return flag;
    }

    @RequestMapping("/getActivityListByNameAndNotByClueId.do")
    @ResponseBody
    private List<Activity> getActivityListByNameAndNotByClueId(String aname,String clueId){
        System.out.println("查询市场活动列表（根据名称模糊查询+排除已经关联指定线索的列表）");

        Map<String,String> map=new HashMap<>();
        map.put("aname",aname);
        map.put("clueId",clueId);

        List<Activity> aList=activityService.getActivityListByNameAndNotByClueId(map);

        return aList;
    }


    @RequestMapping("/bund.do")
    @ResponseBody
    private boolean bund(HttpServletRequest request){
        System.out.println("执行关联市场活动操作");

        String clueId=request.getParameter("clueId");
        String [] activityId=request.getParameterValues("activityId");

        System.out.println("clueId:"+clueId+"======"+"activityId:"+activityId);

        boolean flag=clueService.bund(clueId,activityId);

        return flag;
    }


    @RequestMapping("/getActivityListByName.do")
    @ResponseBody
    private List<Activity> getActivityListByName(String aname){
        System.out.println("查询市场活动列表(根据名称模糊查询)");

        List<Activity> aList=activityService.getActivityListByName(aname);

        return aList;
    }

    @RequestMapping("/convert.do")
    @ResponseBody
    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("执行线索转换操作");

        String clueId=request.getParameter("clueId");

        //接受是否需要创建交易的标记
        String flag=request.getParameter("flag");

        Tran t=null;

        String createBy = ((User)request.getSession().getAttribute("user")).getName();

        //如果需要创建交易
        if("a".equals(flag)){
            t=new Tran();
            //接受交易表单中的参数

            String money = request.getParameter("money");
            String name = request.getParameter("name");
            String expectedDate = request.getParameter("expectedDate");
            String stage = request.getParameter("stage");
            String activityId = request.getParameter("activityId");
            String id = UUIDUtil.getUUID();
            String createTime = DateTimeUtil.getSysTime();


            t.setId(id);
            t.setMoney(money);
            t.setName(name);
            t.setExpectedDate(expectedDate);
            t.setStage(stage);
            t.setActivityId(activityId);
            t.setCreateTime(createTime);
            t.setCreateBy(createBy);
        }



        /**
         * 为业务层传递的参数
         *  1. 必须传递的参数clueId,有了这个clueId之后,我们才知道要转换哪条记录
         *  2. 必须传递的参数t,因为在线索转换的过程中,有可能会临时创建一笔交易(业务层接收的t也有可能是个null)
         */
        boolean flag1=clueService.convert(clueId,t,createBy);

        if(flag1){
            // 重定向
            response.sendRedirect(request.getContextPath()+"/workbench/clue/index.jsp");
        }

    }


}

package com.dhk.crm.workbench.web.controller;

import com.dhk.crm.settings.domain.User;
import com.dhk.crm.settings.service.UserService;
import com.dhk.crm.utils.UUIDUtil;
import com.dhk.crm.workbench.domain.*;
import com.dhk.crm.workbench.service.ActivityService;
import com.dhk.crm.workbench.service.ContactsService;
import com.dhk.crm.workbench.service.CustomerService;
import com.dhk.crm.workbench.service.TranService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/workbench/transaction")
public class TranController {
    @Resource
    private UserService userService;
    @Resource
    private ActivityService activityService;
    @Resource
    private ContactsService contactsService;
    @Resource
    private CustomerService customerService;
    @Resource
    private TranService tranService;


    @RequestMapping("/add.do")
    private ModelAndView add(HttpServletRequest request, HttpServletResponse response){
        System.out.println("进入到跳转交易添加页操作");
        List<User> uList = userService.getUserList();
        ModelAndView mv=new ModelAndView();
        mv.addObject("uList",uList);
        mv.setViewName("forward:/workbench/transaction/save.jsp");
        return mv;

    }

    @RequestMapping("/getActivityListByName.do")
    @ResponseBody
    private List<Activity> getActivityListByName(String aname){
        System.out.println("查询市场活动源列表(根据名称模糊查询)");

        List<Activity> aList=activityService.getActivityListByName(aname);

        return aList;

    }


    @RequestMapping("/getContactsListByName.do")
    @ResponseBody
    private List<Contacts> getContactsListByName(String cname){
        System.out.println("查询联系人列表(根据名称模糊查询)");

        List<Contacts> cList=contactsService.getActivityListByName(cname);

        return cList;

    }


    @RequestMapping("/getCustomerName.do")
    @ResponseBody
    private List<String> getCustomerName(String name){
        System.out.println("取得客户名称列表（按照客户名称进行模糊查询）");
        List<String> sList=customerService.getCustomerName(name);

        return sList;

    }

    @RequestMapping("/save.do")
    @ResponseBody
    private ModelAndView save(Tran tran,String customerName){
        System.out.println("执行添加交易操作");



        ModelAndView mv=new ModelAndView();


        System.out.println("customerName:"+customerName+"--------"+tran.getName());

        boolean flag=tranService.save(tran,customerName);
        if(flag){
            mv.setViewName("redirect:/workbench/transaction/index.jsp");
        }


        return mv;

    }

    @RequestMapping("/detail.do")
    @ResponseBody
    private ModelAndView detail(Tran tran,HttpServletRequest request){
        System.out.println("跳转到详细信息页");

        ModelAndView mv=new ModelAndView();
        String id=tran.getId();

        Tran t=tranService.detail(id);

        //处理可能性
        /*
            阶段 t
            阶段和阶段可能性之间的关系 pMap

         */
        String stage=t.getStage();
        Map<String,String> pMap= (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility=pMap.get(stage);
        System.out.println("possibility----"+possibility);
        t.setPossibility(possibility);


        mv.addObject("t",t);


        mv.setViewName("forward:/workbench/transaction/detail.jsp");



        return mv;

    }

    @RequestMapping("/pageList.do")
    @ResponseBody
    private Map<String,Object> pageList(Tran tran, Integer pageNo, Integer pageSize){
        System.out.println("进入到查询市场交易活动信息列表的操作(结合条件查询+分页查询)");
        String owner = tran.getOwner();
        String name = tran.getName();
        String customerId = tran.getCustomerId();
        String stage = tran.getStage();
        String type = tran.getType();
        String source = tran.getSource();
        String contactsId = tran.getContactsId();

        int skipCount = (pageNo-1) * pageSize;
        Map<String,Object> map = new HashMap<>();
        map.put("pageSize",pageSize);
        map.put("skipCount",skipCount);

        map.put("owner",owner);
        map.put("name",name);
        map.put("customerId",customerId);
        map.put("stage",stage);
        map.put("type",type);
        map.put("source",source);
        map.put("contactsId",contactsId);

        Map<String,Object> tMap = tranService.pageList(map);
        return tMap;


    }


    @RequestMapping("getHistoryListByTranId.do")
    @ResponseBody
    private List<TranHistory> getHistoryListByTranId(String tranId,HttpServletRequest request){
        System.out.println("根据交易id取得相应的历史列表");

        List<TranHistory> thList=tranService.getHistoryListByTranId(tranId);
        //阶段和可能性之间的关系
        Map<String,String> pMap= (Map<String, String>) request.getServletContext().getAttribute("pMap");
        //将交易列表列表遍历
        for(TranHistory th:thList){
            //根据每条交易历史，取出每一个阶段
            String stage = th.getStage();
            String possibility=pMap.get(stage);
            th.setPossibility(possibility);

        }

        return thList;
    }

    @RequestMapping("changeStage.do")
    @ResponseBody
    private Map<String,Object> changeStage(Tran tran,HttpServletRequest request){
        System.out.println("根据交易id取得相应的历史列表");
        System.out.println("-------------"+tran);
        System.out.println("id:"+tran.getId()+"--"+"stage:"+tran.getStage()+"--"+"money:"+tran.getMoney()+"--"+"expectedDate:"+tran.getExpectedDate());


        boolean flag=tranService.changeStage(tran);

        Map<String,String> pMap= (Map<String, String>) request.getServletContext().getAttribute("pMap");
        String possibility=pMap.get(tran.getStage());
        tran.setPossibility(possibility);

        Map<String,Object> map=new HashMap<>();
        map.put("success",flag);
        map.put("t",tran);
        return map;
    }


    @RequestMapping("getCharts.do")
    @ResponseBody
    private Map<String,Object> getCharts(){
        System.out.println("取得交易阶段数量统计图表的数据");
        /*
            业务层为我们返回
                total
                dataList

                通过map打包以上两项数据返回
         */
       Map<String,Object> map=tranService.getCharts();
        return map;
    }
}

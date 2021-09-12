package com.dhk.crm.settings.web.controller;

import com.dhk.crm.exception.MyUserException;
import com.dhk.crm.settings.domain.User;
import com.dhk.crm.settings.service.UserService;
import com.dhk.crm.utils.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService service;
   /* @Resource
    private HttpServletRequest request;
    @Resource
    private HttpServletResponse response;*/

   ////处理器方法返回void， 响应ajax请求
    @RequestMapping(value = "/login.do")
    @ResponseBody
    public Map<String, Object> userLogin(String loginAct, String loginPwd, HttpServletRequest request) throws MyUserException {
        System.out.println("进入到验证登陆操作");
        if(loginPwd==null){
            loginPwd="";
        }
        loginPwd = MD5Util.getMD5(loginPwd);
        //接受浏览器的ip地址
        String ip = request.getRemoteAddr();
        System.out.println("-------------ip:"+ip+":"+loginAct+":"+loginPwd);

        /*//未来业务层开发，统一使用代理形态的接口对象
        String config="config/applicationContext.xml";
        ApplicationContext ac = new ClassPathXmlApplicationContext(config);
        service = (UserService) ac.getBean("userServiceImpl");*/

        //如果发生异常了，在此处交给框架的异常处理器处理ControllerAdvice
        Map<String,Object> map=new HashMap<>();
        User user=service.login(loginAct,loginPwd,ip);
        map.put("success",true);
        request.getSession().setAttribute("user",user);
        System.out.println("user对象:"+user);
        return map;




    }


}

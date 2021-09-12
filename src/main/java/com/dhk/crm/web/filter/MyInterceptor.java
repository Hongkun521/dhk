package com.dhk.crm.web.filter;

import com.dhk.crm.settings.domain.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //拦截器不能拦截静态请求，只能拦截控制器
        //拦截对于访问控制器的请求，判断并且放行
        System.out.println("进入到验证有没有登录过的拦截器");
        String path=request.getServletPath();
        if("/user/login.do".equals(path)){
            System.out.println("允许通过");
            return true;
        }else {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            if(user!=null){
                return true;
            }else {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return false;
            }
        }
    }


}

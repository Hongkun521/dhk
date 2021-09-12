package com.dhk.crm.web.filter;

import com.dhk.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        System.out.println("进入到验证是否登录的过滤器");
        HttpServletRequest request=(HttpServletRequest)req;
        HttpServletResponse response=(HttpServletResponse) resp;

        String path=request.getServletPath();
        System.out.println("path:"+path);
        if("/login.jsp".equals(path) || "/user/login.do".equals(path)){

            chain.doFilter(req,resp);

        }else {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
            System.out.println("用户："+user);

            ///如果user不为null,说明登录过
            if (user != null) {
                System.out.println("请求放行-------");
                chain.doFilter(req,resp);
            } else {
                /*重定向怎么写
                    在实际项目开发的过程中，对于路径的使用，不论是前段操作还是后端，一律使用绝对路径
                    关于转发的重定向的路径写法如下：
                    转发：
                        使用的是一种特殊的绝对路径的使用方法，这种绝对路径前面不加/项目名，这种路径也称之为内部路径/login.jsp
                    重定向：
                        使用的是传统的绝对路径的写法，前面必须以/项目名开头，后面跟具体的资源路径
                        /crm/login.jsp
                     为何使用转发，不使用重定向？
                     转发之后，路径会停留在老路径上，而不是转跳之后最新资源的路径
                     我们应该在用户跳转到登陆页的时候，将浏览器地址栏自动设置为当前登陆页的路径
                */
                //执行了重定向方法
                response.sendRedirect(request.getContextPath() + "/login.jsp");

            }
        }


    }

    @Override
    public void destroy() {

    }


}

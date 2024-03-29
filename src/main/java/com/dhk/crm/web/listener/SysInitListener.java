package com.dhk.crm.web.listener;

import com.dhk.crm.settings.domain.DicValue;
import com.dhk.crm.settings.service.DicService;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class SysInitListener implements ServletContextListener {
    /**
     * 该方法是用来监听上下文域对象的方法,当服务器启动,上下文域对象创建
     * 对象创建完毕后,马上执行该方法
     *
     * event: 该参数能够取得监听的对象
     *      监听的是什么对象,就可以通过该参数取得什么对象
     *      例如我们现在监听的是上下文域对象,通过该参数就可以取得上下文域对象
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        System.out.println("创建了上下文域对象");
        ServletContext application=event.getServletContext();

        DicService dicService = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()).getBean(DicService.class);
        /**
         * 应该管业务层要什么
         *  7个List
         *
         *  可以打包称为一个map
         *  业务层应该是这样来保存数据的:
         *      map.put("appellationList",dvList1);
         *      map.put("clueStateList",dvList2);
         *      map.put("stageList",dvList3);
         */
        Map<String, List<DicValue>> map=new HashMap<>();
        //取数据字典
        map=dicService.getAll(application);
        // 将map解析为上下文域对象中保存的键值对
        Set<String> set = map.keySet();
        for(String key:set){
            application.setAttribute(key,map.get(key));
        }

        System.out.println("服务器缓存处理数据字典结束");

        //---------------------------------------------------
        //数据字典处理完毕后，处理stage2Possibility.properties文件

        /*
        处理stage2Possibility.properties文件步骤:
            解析该文件，将该属性文件中的键值对关系处理成java中的键值对关系（map）
            Map<String(阶段stage),String(可能性possibility)> pMap=...
            pMap.put("01资质审查","10");
            ...

            pMap保存之后，放在服务器缓存中
            application.setAttribute(pMap);

         */
        Map<String,String> pMap=new HashMap<>();

        ResourceBundle bundle=ResourceBundle.getBundle("Stage2Possibility");
        Enumeration<String> keys = bundle.getKeys();

        while(keys.hasMoreElements()){
            //阶段
            String key=keys.nextElement();
            //可能性
            String value=bundle.getString(key);
            pMap.put(key,value);
        }

        //将pMap保存到服务器缓存中
        application.setAttribute("pMap",pMap);
        System.out.println("服务器pMap保存到服务器缓存中结束:"+pMap);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}

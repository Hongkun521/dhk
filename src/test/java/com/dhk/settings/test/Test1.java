package com.dhk.settings.test;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Test1 {
    @Test
    public void test1(){
        //验证失效时间
        String expireTime="2019-10-10 10:10:10";
        Date date=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String str = sdf.format(date);

        String LockState="0";
        if("0".equals(LockState)){
            System.out.println("账号已锁定");
        }

        //浏览器ip地址
        String ip="192.168.1.1";
        String allowIps="192.168.1.1,192.168.1.2";
        if(allowIps.contains(ip)){
            System.out.println("有效ip地址，允许访问系统");
        }else{
            System.out.println("ip地址受限，不允许访问系统");
        }
    }
}

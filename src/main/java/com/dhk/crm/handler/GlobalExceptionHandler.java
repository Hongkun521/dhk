package com.dhk.crm.handler;

import com.dhk.crm.exception.LoginException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = LoginException.class)
    @ResponseBody
    public Map<String,Object> doLoginException(Exception e){
        e.printStackTrace();
        String msg=e.getMessage();
        Map<String,Object> map=new HashMap<>();
        map.put("success",false);
        map.put("msg",msg);
        return map;
    }
}

package com.qingcheng.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-29 22:03
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @RequestMapping("/getUsername")
    public Map getUsername(){
        //得到登陆人账户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map map = new HashMap();
        if ("anonymousUser".equals(username)){
            username = "";
        }
        map.put("username",username);
        return map;
    }
}

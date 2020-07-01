package com.qingcheng.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-22 11:14
 */
@RestController
@RequestMapping("/login")
public class LoginController {


    @GetMapping("/findName.do")
    public Map getName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        HashMap hashMap = new HashMap();
        hashMap.put("username",name);
        return hashMap;
    }

}

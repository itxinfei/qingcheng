package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.user.User;
import com.qingcheng.service.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 戴金华
 * @date 2019-11-29 14:36
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/sendSms")
    public Result sendSms(String phone){
        try {
            System.out.println(phone);
            userService.sendSms(phone);
            return new Result(0,"验证码发送成功");
        }catch (Exception e){
            return new Result(1,"验证码发送失败");
        }
    }

    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smsCode){
        //密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        try {
            userService.add(user,smsCode);
            return new Result(0,"注册成功");
        }catch (Exception e){
            return new Result(1,e.getMessage());
        }
    }
}

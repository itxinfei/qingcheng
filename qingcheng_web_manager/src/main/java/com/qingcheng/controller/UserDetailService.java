package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.service.system.AdminService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-22 9:53
 */
public class UserDetailService implements UserDetailsService {


    @Reference
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        //查询管理员
        Map map = new HashMap();
        map.put("loginName",s);
        map.put("status",1);

        List<Admin> adminList = adminService.findList(map);
        if (adminList.size()==0){
            //如果查询结果为空
            return null;
        }

        //构建角色集合 此处根据用户名查询用户的角色列表
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return new User(s,adminList.get(0).getPassword(),grantedAuthorities);
    }
}

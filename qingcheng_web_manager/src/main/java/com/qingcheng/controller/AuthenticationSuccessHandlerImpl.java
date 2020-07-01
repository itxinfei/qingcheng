package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.system.LoginLog;
import com.qingcheng.service.system.LoginLogService;
import com.qingcheng.util.WebUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author 戴金华
 * @date 2019-11-22 15:02
 */
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {


    @Reference
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //登录成功之后会调用 登录成功处理器

        //逻辑 将数据插入tb_login_log
        LoginLog loginLog = new LoginLog();

        //获得登录名
        String name = authentication.getName();
        loginLog.setLoginName(name);
        loginLog.setLoginTime(new Date());

        //获得访问的ip
        String ip = httpServletRequest.getRemoteAddr();
        loginLog.setIp(ip);

        //获取访问城市
        String city = WebUtil.getCityByIP(ip);
        loginLog.setLocation(city);

        //获取浏览器名称
        String agent = httpServletRequest.getHeader("user-agent");
        String browserName = WebUtil.getBrowserName(agent);
        loginLog.setBrowserName(browserName);
        loginLogService.add(loginLog);
        httpServletRequest.getRequestDispatcher("/main.html").forward(httpServletRequest,httpServletResponse);
    }
}

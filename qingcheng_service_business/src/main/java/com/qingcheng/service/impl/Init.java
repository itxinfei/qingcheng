package com.qingcheng.service.impl;

import com.qingcheng.service.business.AdService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 戴金华
 * @date 2019-11-23 14:42
 */
@Component
public class Init implements InitializingBean {

    @Autowired
    private AdService adService;

    @Override
    public void afterPropertiesSet() throws Exception {
        //将图片资源放入缓存中
        System.out.println("开始缓存图片资源");
        adService.saveToRedis();
    }
}

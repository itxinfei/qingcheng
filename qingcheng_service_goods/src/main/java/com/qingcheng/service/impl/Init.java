package com.qingcheng.service.impl;

import com.qingcheng.service.goods.CategoryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 戴金华
 * @date 2019-11-23 11:31
 */
@Component
public class Init implements InitializingBean {

    @Autowired
    private CategoryService categoryService;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("缓存预热开始");
        categoryService.saveCategoryTreeToRedis();
    }
}

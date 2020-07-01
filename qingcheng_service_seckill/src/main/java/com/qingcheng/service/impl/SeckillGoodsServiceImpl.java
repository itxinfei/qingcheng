package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.service.seckill.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * @author 戴金华
 * @date 2019-12-01 11:44
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> findList(String key) {
        return redisTemplate.boundHashOps("SeckillGoods_"+key).values();
    }
}

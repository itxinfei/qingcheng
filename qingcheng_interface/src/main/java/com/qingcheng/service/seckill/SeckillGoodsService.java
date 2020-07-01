package com.qingcheng.service.seckill;

import com.qingcheng.pojo.seckill.SeckillGoods;

import java.util.List;

/**
 * @author 戴金华
 * @date 2019-12-01 11:44
 */
public interface SeckillGoodsService {

    /**
     * 获得指定时间段的秒杀商品
     * @param key
     * @return
     */
    List<SeckillGoods> findList(String key);
}

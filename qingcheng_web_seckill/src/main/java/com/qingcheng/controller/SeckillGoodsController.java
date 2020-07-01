package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.service.seckill.SeckillGoodsService;
import com.qingcheng.util.DateUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * @author 戴金华
 * @date 2019-12-01 14:43
 */
@RestController
@RequestMapping("/seckill")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    @RequestMapping("/getDateMenus")
    public List<Date> getDateMenus(){
        return DateUtil.getDateMenus();
    }


    @RequestMapping("/getSeckillGoodsByTime")
    public List<SeckillGoods> getSeckillGoodsByTime(String time){
        List<SeckillGoods> list = seckillGoodsService.findList(DateUtil.formatStr(time));
        return list;
    }
}

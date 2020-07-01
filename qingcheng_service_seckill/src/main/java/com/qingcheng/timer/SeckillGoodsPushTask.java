package com.qingcheng.timer;

import com.qingcheng.dao.SeckillGoodsMapper;
import com.qingcheng.pojo.seckill.SeckillGoods;
import com.qingcheng.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author 戴金华
 * @date 2019-12-01 10:52
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定时任务 将符合条件的商品放入缓存中
     * 1.查询活动没结束的所有秒杀商品
     * 1)计算秒杀时间段
     * 2)状态必须为审核通过 status=1
     * 3)商品库存个数>0
     * 4)活动没有结束 endTime>=now()
     * 5)在Redis中没有该商品的缓存
     * 6)执行查询获取对应的结果集
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void  loadGoodsPushRedis(){
        //获取时间段的集合
        List<Date> dateMenus = DateUtil.getDateMenus();

        //循环时间段
        for (Date startTime : dateMenus) {
            String s = DateUtil.date2Str(startTime);

            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("status","1");  //审核状态必须为通过
            criteria.andGreaterThan("stockCount",0);    //剩余库存数量必须大于0
            criteria.andGreaterThanOrEqualTo("startTime",startTime);   //开始时间必须大于等于
            criteria.andLessThan("endTime",DateUtil.addDateHour(startTime,2)); //结束时间必须小于等于开始时间+2

            //确保缓存中没有该商品的缓存
            Set keys = redisTemplate.boundHashOps("SeckillGoods_" + s).keys();
            if (keys!=null&&keys.size()>0){
                criteria.andNotIn("id",keys);
            }

            //从数据库中查询数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            //将查询到的数据放入 缓存中
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps("SeckillGoods_"+s).put(seckillGood.getId(),seckillGood);
            }
        }
    }
}

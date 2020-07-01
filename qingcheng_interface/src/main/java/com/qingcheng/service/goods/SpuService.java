package com.qingcheng.service.goods;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.goods.Goods;
import com.qingcheng.pojo.goods.Spu;

import java.util.*;

/**
 * spu业务逻辑层
 */
public interface SpuService {


    public List<Spu> findAll();


    public PageResult<Spu> findPage(int page, int size);


    public List<Spu> findList(Map<String,Object> searchMap);


    public PageResult<Spu> findPage(Map<String,Object> searchMap,int page, int size);


    public Spu findById(String id);

    public void add(Spu spu);


    public void update(Spu spu);


    public void delete(String id);

    /**
     * 保存商品
     * @param goods
     */
    public void saveGoods(Goods goods);

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    public Goods findGoodsById(String id);

    /**
     * 审核商品
     * @param id
     * @param status    审核状态
     * @param message   审核消息
     */
    public void audit(String id ,String status,String message);

    /**
     * 查询商品回收站里的商品
     * @return
     */
    public List<Goods> findDeleteGoods();


    /**
     * 还原商品
     * @param id
     */
    public void restoreGoods(String id);

    /**
     * 删除商品
     */
    public void deleteGoods(String id);
}

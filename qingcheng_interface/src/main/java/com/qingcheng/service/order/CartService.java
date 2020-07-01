package com.qingcheng.service.order;

import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-30 11:32
 */
public interface CartService {

    /**
     * 根据用户名查找购物车列表
     * @param username
     * @return
     */
    public List<Map<String,Object>> findCartList(String username);


    /**
     * 将商品添加到购物车中
     * @param username
     * @param skuId
     * @param num
     */
    public void addCart(String username,String skuId,Integer num);

    /**
     * 更新勾选状态
     * @param username
     * @param skuId
     * @param checked
     */
    public void updateChecked(String username,String skuId,boolean checked);

    /**
     * 删除选中的购物车
     * @param username
     */
    public void deleteCart(String username);
}

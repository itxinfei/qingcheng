package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.goods.Category;
import com.qingcheng.pojo.goods.Sku;
import com.qingcheng.pojo.order.OrderItem;
import com.qingcheng.service.goods.CategoryService;
import com.qingcheng.service.goods.SkuService;
import com.qingcheng.service.order.CartService;
import com.qingcheng.util.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 戴金华
 * @date 2019-11-30 11:34
 */
@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private SkuService skuService;

    @Reference
    private CategoryService categoryService;

    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println("从redis中提取购物车"+username);
        List<Map<String,Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART_LIST).get(username);

        if (cartList==null){
            return new ArrayList<>();
        }
        return cartList;
    }

    /**
     * 将商品添加到购物车中
     * @param username
     * @param skuId
     * @param num
     */
    @Override
    public void addCart(String username, String skuId, Integer num) {
        //遍历购物车 如果购物车中存在该商品 则商品数量+1 如果不存在该商品 则添加
        List<Map<String, Object>> cartList = findCartList(username);

        boolean flag = false;
        for (Map<String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            if (skuId.equals(orderItem.getSkuId())){
                //如果购物车中存在该商品
                if (orderItem.getNum()<=0){
                    //如果购物车中的商品数量小于等于0
                    cartList.remove(map);
                    break;
                }

                int weight = orderItem.getWeight()/orderItem.getNum();  //单个商品的重量
                orderItem.setWeight(orderItem.getWeight()+weight*num);
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setMoney(orderItem.getPrice()*orderItem.getNum());

                if (orderItem.getNum()<=0){
                    cartList.remove(map);
                }

                flag = true;
                break;
            }
        }

        if (!flag){
            //如果购物车中不存在该商品 直接添加
            Sku sku = skuService.findById(skuId);

            //合法校验
            if (sku==null){
                throw new RuntimeException("商品不存在");
            }

            if (!"1".equals(sku.getStatus())){
                throw new RuntimeException("商品已下架");
            }

            if (sku.getNum()<=0){
                throw new RuntimeException("商品数量不能为空");
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setSkuId(skuId);
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setName(sku.getName());   //商品名称
            orderItem.setPrice(sku.getPrice()); //单价
            orderItem.setNum(num);              //商品数量
            orderItem.setMoney(num*sku.getPrice()); //总金额
            orderItem.setImage(sku.getImage());

            if (sku.getWeight()==null){
                sku.setWeight(0);
            }
            orderItem.setWeight(sku.getWeight()*num);
            orderItem.setCategoryId3(sku.getCategoryId());
            Category category3 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(sku.getCategoryId());
            if (category3==null){
                //如果缓存中没有 就查询数据库
                category3 = categoryService.findById(sku.getCategoryId());
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(sku.getCategoryId(),category3);
            }
            orderItem.setCategoryId2(category3.getParentId());

            Category category2 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(orderItem.getCategoryId2());
            if (category2==null){
                category2 = categoryService.findById(orderItem.getCategoryId2());
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(orderItem.getCategoryId2(),category2);
            }

            orderItem.setCategoryId1(category2.getParentId());

            Map map = new HashMap();
            map.put("item",orderItem);
            map.put("checked",true);        //购物车栏默认被选中

            //将新加的商品放到缓存中
            cartList.add(map);
        }

        //更新购物车
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username,cartList);
    }


    //更新勾选状态
    @Override
    public void updateChecked(String username, String skuId, boolean checked) {
        List<Map<String, Object>> cartList = findCartList(username);
        for (Map<String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            if (skuId.equals(orderItem.getSkuId())){
                map.put("checked",checked);
                break;
            }
        }
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username,cartList);
    }


    /**
     * 删除选中的购物车
     * @param username
     */
    @Override
    public void deleteCart(String username) {
        //获得未被选中的购物项
        List<Map<String, Object>> cartList = findCartList(username).stream().filter(cart -> (boolean) cart.get("checked") == false).collect(Collectors.toList());
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username,cartList);
    }
}

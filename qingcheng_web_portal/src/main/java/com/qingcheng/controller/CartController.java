package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.Result;
import com.qingcheng.service.order.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-30 11:43
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @RequestMapping("/findCartList")
    public List<Map<String,Object>> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> cartList = cartService.findCartList(username);
        return cartList;
    }


    @RequestMapping("/addCart")
    public Result addCart(String skuId,Integer num){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            cartService.addCart(username,skuId,num);
            return new Result(0,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(1,e.getMessage());
        }
    }

    @RequestMapping("/buy")
    public void buy(HttpServletResponse response,String skuId,Integer num) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addCart(username,skuId,num);
        //重定向到购物城页面
        response.sendRedirect("/cart.html");
    }


    @RequestMapping("/updateChecked")
    public Result updateChecked(String skuId,boolean checked){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            cartService.updateChecked(username,skuId,checked);
            return new Result(0,"跟新成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(1,"更新失败");
        }
    }


    @RequestMapping("/deleteCart")
    public Result deleteCart(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            cartService.deleteCart(username);
            return new Result(0,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(1,"删除失败");
        }
    }
}

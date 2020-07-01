package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.business.Ad;
import com.qingcheng.service.business.AdService;
import com.qingcheng.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-10-28 15:42
 */
@Controller
public class IndexController {

    @Reference
    private AdService adService;
    @Reference
    private CategoryService categoryService;

    @GetMapping("/index")
    public String index(Model model){
        List<Ad> ads = adService.findByPosition("web_index_lb");
        model.addAttribute("ads",ads);


        List<Map> categoryTree = categoryService.findCategoryTree();
        model.addAttribute("categoryTree",categoryTree);
        return "index";
    }
}

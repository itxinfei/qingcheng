package com.qingcheng.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.service.goods.SkuSearchService;
import com.qingcheng.util.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-11-25 20:10
 */
@Controller
public class SearchController {

    @Reference
    private SkuSearchService skuSearchService;

    @GetMapping("/search")
    public String search(Model model,@RequestParam Map<String,String> searchMap) throws Exception {
        searchMap = WebUtil.convertCharsetToUTF8(searchMap);


        if (searchMap.get("pageNo")==null){
            //如果页码为空 设置为1
            searchMap.put("pageNo","1");
        }


        //排序参数容错处理
        if (searchMap.get("sort")==null){
            searchMap.put("sort","");
        }
        if(searchMap.get("sortOrder")==null){
            searchMap.put("sortOrder","DESC");
        }

        //远程调用
        Map result = skuSearchService.search(searchMap);
        model.addAttribute("result",result);
        //url处理
        StringBuffer url = new StringBuffer("/search.do?");
        for (String key : searchMap.keySet()) {
            url.append("&"+key+"="+searchMap.get(key));
        }

        int pageNo = Integer.parseInt(searchMap.get("pageNo"));
        Long totalPages = (Long) result.get("totalPages");
        int startPage = 1;  //开始页码
        int endPage = totalPages.intValue();    //末尾页码
        if (totalPages>5){
            startPage = pageNo-2;
            endPage = pageNo+2;

            if (startPage<1){
                startPage = 1;
                endPage = startPage+4;
            }

            if (endPage>totalPages){
                endPage = totalPages.intValue();
                startPage = endPage-4;
            }
        }

        //将请求回写给前台
        model.addAttribute("searchMap",searchMap);
        model.addAttribute("url",url);
        model.addAttribute("pageNo",pageNo);    //当前页码
        model.addAttribute("startPage",startPage);  //开始页码
        model.addAttribute("endPage",endPage);      //结束页码
        return "search";
    }
}

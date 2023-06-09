package com.qingcheng.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-10-29 18:26
 */
@RestController
@RequestMapping("/categoryReport")
public class CategoryReportController {


    @Reference
    private CategoryReportService categoryReportService;



    @GetMapping("/yesterday")
    public List<CategoryReport> yesterday(){
        LocalDate date = LocalDate.of(2019, 4, 15);
        return categoryReportService.findAll(date);
    }

    @GetMapping("/category1Count")
    public List<Map> category1Count(String date1,String date2){
        return categoryReportService.category1Count(date1,date2);
    }
}

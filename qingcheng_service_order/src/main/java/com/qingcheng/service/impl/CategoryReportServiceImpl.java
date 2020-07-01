package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.dao.CategoryReportMapper;
import com.qingcheng.pojo.order.CategoryReport;
import com.qingcheng.service.order.CategoryReportService;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-10-29 16:03
 */
@Service
public class CategoryReportServiceImpl implements CategoryReportService {


    @Autowired
    private CategoryReportMapper categoryReportMapper;

    //查找所有
    @Override
    public List<CategoryReport> findAll(LocalDate date) {
        return categoryReportMapper.findAll(date);
    }

    @Override
    public List<Map> category1Count(String date1, String date2) {
        return categoryReportMapper.category1Count(date1,date2);
    }
}

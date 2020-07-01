package com.qingcheng.service.order;

import com.qingcheng.pojo.order.CategoryReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-10-29 16:02
 */
public interface CategoryReportService {

    public List<CategoryReport> findAll(LocalDate date);

    public List<Map> category1Count(String date1,String date2);
}

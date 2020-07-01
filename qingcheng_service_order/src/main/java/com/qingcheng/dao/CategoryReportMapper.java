package com.qingcheng.dao;

import com.qingcheng.pojo.order.CategoryReport;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author 戴金华
 * @date 2019-10-29 15:56
 */
public interface CategoryReportMapper extends Mapper<CategoryReport> {


    @Select("SELECT  oi.category_id1 categoryId1,oi.category_id2 categoryId2,oi.category_id3 categoryId3,DATE_FORMAT(o.pay_time,'%Y-%m-%d') countDate,SUM(oi.`pay_money`) money,SUM(oi.`num`) num " +
            "FROM tb_order o,tb_order_item oi " +
            "WHERE oi.`order_id`=o.`id` AND o.`pay_status` = '1' " +
            "AND DATE_FORMAT(o.pay_time,'%Y-%m-%d')=#{date} " +
            "GROUP BY " +
            "oi.`category_id1`,oi.`category_id2`,oi.`category_id3`,DATE_FORMAT(o.pay_time,'%Y-%m-%d')")
    public List<CategoryReport> findAll(@Param("date") LocalDate date);


    @Select("SELECT category_id1 categoryId1,NAME category1Name,count_date,num,money " +
            "FROM tb_category_report cr,v_category1 v " +
            "WHERE cr.category_id1=v.id AND " +
            "count_date>=#{date1} AND count_date<=#{date2}" +
            "GROUP BY categoryId1,category1Name")
    public List<Map> category1Count(@Param("date1") String date1,@Param("date2") String date2);
}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.PlanUs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

public interface PlanUsMapper extends BaseMapper<PlanUs> {

    @Select("select count(*) from plan_us where date >= #{date}")
    Integer getGtDateCount(@Param("date") LocalDate date);

    @Select("select max(date) from plan_us")
    LocalDate getMaxDate();
}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Material;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MaterialMapper extends BaseMapper<Material> {

    @Select("select  max(date) from material ")
    LocalDate getMaxDate();

    @Select("select count(*) from material where date=#{date}")
    BigDecimal getCount(@Param("date") LocalDate date);

}

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

    @Select("select count(*) from material where date=#{date} and `name` LIKE CONCAT('%',#{name},'%')")
    BigDecimal getCount(@Param("date") LocalDate date, @Param("name") String name);

    @Select("select identifier from material where `name` =#{name} limit 1")
    String getIdByName(@Param("name") String name);

}

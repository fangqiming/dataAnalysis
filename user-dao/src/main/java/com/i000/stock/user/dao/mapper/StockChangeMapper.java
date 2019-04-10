package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.StockChange;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface StockChangeMapper extends BaseMapper<StockChange> {

    @Select("select max(date) from stock_change where code = #{code}")
    LocalDate getMaxDateByCode(@Param("code") String code);

    @Select("select sum(change_number) from stock_change  where `code`=#{code}")
    BigDecimal getChangeNumber(@Param("code") String code);
}

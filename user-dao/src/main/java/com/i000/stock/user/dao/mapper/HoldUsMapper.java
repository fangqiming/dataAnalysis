package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.HoldUs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface HoldUsMapper extends BaseMapper<HoldUs> {

    @Select("select old_price from hold_us where old_date = #{date} and code = #{code} limit 1")
    BigDecimal getOldPriceByDateAndCode(@Param("date") LocalDate date, @Param("code") String code);

}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.HoldNowUs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.tomcat.jni.Local;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface HoldNowUsMapper extends BaseMapper<HoldNowUs> {

    @Update("update hold_now_us set amount = (old_price * amount)/#{cost} ,old_price = #{cost} where code = #{code} and old_date = #{oldDate} ")
    void handleShareSplitUp(@Param("cost") BigDecimal cost, @Param("code") String code, @Param("oldDate") LocalDate oldDate);

    @Update("update hold_now_us set new_price = #{price}, new_date = #{date} where code = #{code}")
    void updateSharePriceAndDate(@Param("price") BigDecimal cost, @Param("date") LocalDate date, @Param("code") String code);

    @Select("select count(*) from hold_now_us")
    BigDecimal getHoldCount();

    @Select("select count(*) from hold_now_us  where type!='SHORT'")
    BigDecimal getHoldLongCount();

    @Select("select sum(new_price*amount) from hold_now_us where user=#{user} and type in ('LONG1','LONG2')")
    BigDecimal getStock(@Param("user") String user);

    @Select("select sum(new_price*amount)*-1 from hold_now_us where user=#{user} and type in ('SHORT')")
    BigDecimal getCover(@Param("user") String user);
}

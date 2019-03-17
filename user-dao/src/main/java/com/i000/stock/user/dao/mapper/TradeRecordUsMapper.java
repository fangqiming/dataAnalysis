package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.TradeRecordUs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface TradeRecordUsMapper extends BaseMapper<TradeRecordUs> {

    @Select("select count(*) from trade_record_us where \n" +
            "(new_price > old_price and action = 'SELL' and user=#{user}) or (new_price < old_price and action = 'COVER' and user=#{user})")
    BigDecimal getEarnMoneyNumber(@Param("user") String user);

    @Select("select count(*) from trade_record_us where user = #{user} and action in ('SELL','COVER')")
    BigDecimal getCoverAndSellNumber(@Param("user") String user);

    @Select("select avg(datediff(new_date,old_date)) from trade_record_us where user=#{user} and action in ('SELL','COVER')")
    BigDecimal getAvgHoldDay(@Param("user") String user);

    @Select("select count(*) from trade_record_us where new_price < old_price and action = 'COVER' and user=#{user}")
    BigDecimal getShortEarnMoneyNumber(@Param("user") String user);
}

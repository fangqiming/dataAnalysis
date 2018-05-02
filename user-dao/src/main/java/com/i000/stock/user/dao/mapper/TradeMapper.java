package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Plan;
import com.i000.stock.user.dao.model.Trade;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:19 2018/4/26
 * @Modified By:
 */
public interface TradeMapper extends BaseMapper<Trade> {

    /**
     * 根据日期查询当天的交易记录
     *
     * @param date
     * @return
     */
    List<Trade> findByDate(@Param("date") LocalDate date);

    /**
     * 查询最大的发生过得交易
     *
     * @return
     */
    @Select("select max(date) from trade")
    LocalDate getMaxDate();

    /**
     * 根据股票名称获取股票价格
     *
     * @param name
     * @return
     */
    @Select("select price from trade where `name`=#{name} and action='SELL' order by id limit 1")
    BigDecimal getPriceByName(@Param("name") String name);

    /**
     * 获取平仓的股票价格
     *
     * @param name
     * @return
     */
    @Select("select price from trade where `name`=#{name} and action='COVER' order by id limit 1")
    BigDecimal getCoverPriceByName(@Param("name") String name);

}

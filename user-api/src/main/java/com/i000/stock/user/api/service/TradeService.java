package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.Trade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:51 2018/4/27
 * @Modified By:
 */
public interface TradeService {

    /**
     * 查询指定的交易日期的交易记录
     *
     * @param date
     * @return
     */
    List<Trade> findByDate(LocalDate date);

    /**
     * 查询指定日期的交易记录
     *
     * @param dates
     * @return
     */
    List<Trade> findByDate(List<LocalDate> dates);

    /**
     * 查询最新的交易日期
     *
     * @return
     */
    LocalDate getMaxDate();

    /**
     * 获取指定股票的最新价格
     *
     * @param name
     * @return
     */
    BigDecimal getSellPrice(String name);

    /**
     * 获取指定股票的最新价格
     *
     * @param name
     * @return
     */
    BigDecimal getCoverPrice(String name);

}

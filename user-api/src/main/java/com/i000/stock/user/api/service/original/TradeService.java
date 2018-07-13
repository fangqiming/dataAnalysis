package com.i000.stock.user.api.service.original;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:51 2018/4/27
 * @Modified By:
 */
public interface TradeService {

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

    /**
     * 处理由于拆股导致的价格变化
     *
     * @param name
     */
    void updatePrice(String name, BigDecimal rate);

}

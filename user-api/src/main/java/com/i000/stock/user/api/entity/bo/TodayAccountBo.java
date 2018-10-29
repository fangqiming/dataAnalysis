package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:58 2018/10/24
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayAccountBo {

    /**
     * 当天日期
     */
    private String date;

    /**
     * 当天总资产
     */
    private BigDecimal totalAsset;

    /**
     * 当天浮动盈亏
     */
    private BigDecimal relativeProfit;

    /**
     * 当天浮动盈亏率
     */
    private BigDecimal relativeProfitRate;

    /**
     * 跑赢上证指数
     */
    private BigDecimal beatStandardRate;

    /**
     * 当前仓位
     */
    private BigDecimal position;

    /**
     * 持股市值
     */
    private BigDecimal stockMarket;

    /**
     * 账户余额
     */
    private BigDecimal balance;
}

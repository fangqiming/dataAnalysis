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
 * @Date:Created in 16:50 2018/7/3
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndAssetBo {
    /**
     * 当前日期
     */
    private String date;

    /**
     * 总资产
     */
    private BigDecimal totalAsset;

    /**
     * 持股价值
     */
    private BigDecimal stockAmount;

    /**
     * 账户余额
     */
    private BigDecimal balanceAmount;

    /**
     * 总盈亏率
     */
    private BigDecimal totalProfit;

    /**
     * 今日仓位
     */
    private BigDecimal todayPosition;

    /**
     * 每手金额数量
     */
    private BigDecimal shareAmount;
}

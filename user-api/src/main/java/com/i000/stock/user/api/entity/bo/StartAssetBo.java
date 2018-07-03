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
 * @Date:Created in 16:46 2018/7/3
 * @Modified By:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartAssetBo {

    /**
     * 开始日期
     */
    private String date;

    /**
     * 总资产
     */
    private BigDecimal totalAsset;

    /**
     * 股票金额
     */
    private BigDecimal stockAmount;

    /**
     * 账户余额
     */
    private BigDecimal balanceAmount;

    /**
     * 今日盈亏率
     */
    private BigDecimal todayProfit;

    /**
     * 平均仓位
     */
    private BigDecimal avgPosition;
}

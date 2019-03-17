package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 19:05 2018/10/24
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalAccountBo {

    private String date;

    private BigDecimal initAmount;

    private BigDecimal relativeProfit;

    private BigDecimal relativeProfitRate;

    private BigDecimal beatStandardRate;

    private BigDecimal avgPosition;

    private BigDecimal repoProfit;

    private BigDecimal repoProfitRate;

    private BigDecimal maxWithdrawal;

    private BigDecimal shortRate;
}

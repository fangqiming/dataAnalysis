package com.i000.stock.user.api.entity.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 19:05 2018/10/24
 * @Modified By:
 */
@Data
public class TotalAccountBo {

    private LocalDate date;

    private BigDecimal initAmount;

    private BigDecimal relativeProfit;

    private BigDecimal relativeProfitRate;

    private BigDecimal beatStandardRate;

    private BigDecimal avgPosition;

    private BigDecimal repoProfit;

    private BigDecimal repoProfitRate;

}

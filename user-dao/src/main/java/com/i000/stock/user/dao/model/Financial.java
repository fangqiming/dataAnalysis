package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description: 财务数据对象
 * @Date:Created in 13:50 2018/7/25
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Financial {
    private LocalDate day;
    private String code;
    private BigDecimal grossProfitMargin;
    private BigDecimal netProfitMargin;
    private BigDecimal incRevenueYearOnYear;
    private BigDecimal operatingRevenue;
    private BigDecimal netProfit;
    private BigDecimal inventories;
    private BigDecimal accountReceivable;
    private BigDecimal netOperateCashFlow;
    private BigDecimal cashAndEquivalentsAtEnd;
    private BigDecimal cashEquivalentIncrease;
    private BigDecimal goodWill;
    private BigDecimal totalLiability;
    private BigDecimal totalAssets;
    private BigDecimal marketCap;
    private BigDecimal nonOperatingRevenue;
    private BigDecimal roe;
}

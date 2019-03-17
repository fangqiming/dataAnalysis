package com.i000.stock.user.api.entity.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FinancialBo {

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate day;
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

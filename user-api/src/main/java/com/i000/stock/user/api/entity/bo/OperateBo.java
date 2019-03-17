package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperateBo {
    private String dateRange;
    private BigDecimal grossProfitMarginAvg;
    private BigDecimal grossProfitMarginGrowthRate;
    private BigDecimal roeAvg;
    private BigDecimal roeGrowthRate;
    private BigDecimal operatingRevenueAvg;
    private BigDecimal operatingRevenueGrowthRate;
}

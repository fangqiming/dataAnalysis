package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialNow {

    private LocalDate day;

    private String code;

    private BigDecimal goodWillRate;

    private BigDecimal netOperateCashFlowRate;

    private BigDecimal debtRate;

    private BigDecimal marketCap;

    private BigDecimal grossProfitMargin;

    private BigDecimal peRatio;

}

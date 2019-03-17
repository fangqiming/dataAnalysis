package com.i000.stock.user.api.entity.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
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
public class FinancialRiskBo {
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private BigDecimal pledgeRate;
    private String pledgeRateMsg;
    private BigDecimal goodwillRate;
    private String goodwillRateMsg;
    private BigDecimal netOperateCashFlowRate;
    private String netOperateCashFlowRateMsg;
    private BigDecimal debtRate;
    private String debtRateMsg;
}

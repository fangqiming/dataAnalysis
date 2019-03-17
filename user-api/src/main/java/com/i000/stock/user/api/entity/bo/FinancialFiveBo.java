package com.i000.stock.user.api.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinancialFiveBo {

    private String title;
    private BigDecimal k0;
    private BigDecimal k1;
    private BigDecimal k2;
    private BigDecimal k3;
    private BigDecimal k4;

}

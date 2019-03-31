package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestorLogVO {

    private String name;

    private BigDecimal share;

    private BigDecimal amount;
}

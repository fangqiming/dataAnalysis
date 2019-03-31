package com.i000.stock.user.dao.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestorLogBo {

    private String name;

    private BigDecimal share;

    private BigDecimal amount;
}

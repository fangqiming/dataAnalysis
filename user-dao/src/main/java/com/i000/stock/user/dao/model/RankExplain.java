package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankExplain {

    private BigDecimal score;

    private BigDecimal winRate;

    private BigDecimal maxProfit;

    private BigDecimal minProfit;
}

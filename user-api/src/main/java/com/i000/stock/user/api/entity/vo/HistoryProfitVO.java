package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryProfitVO {
    private String title;
    private BigDecimal gain;
    private BigDecimal szGain;
    private BigDecimal beta;
}

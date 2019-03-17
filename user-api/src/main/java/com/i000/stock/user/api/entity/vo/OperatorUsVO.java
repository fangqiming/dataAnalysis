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
public class OperatorUsVO {

    private Integer sellNumber;

    private Integer buyNumber;

    private Integer shortNumber;

    private Integer coverNumber;

    private Integer tradeNumber;

    private Integer holdNumber;

    private Integer profitNumber;

    private Integer lossNumber;

    private BigDecimal avgHoldDay;

    private BigDecimal winRate;

}

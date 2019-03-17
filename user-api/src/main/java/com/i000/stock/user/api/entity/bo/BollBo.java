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
public class BollBo {

    private BigDecimal mid;

    private BigDecimal up;

    private BigDecimal low;

    private BigDecimal lowPrice;

    private BigDecimal close;
}

package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:34 2018/7/4
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatorVo {

    private Integer sellNumber;

    private Integer buyNumber;

    private Integer profitNumber;

    private Integer lossNumber;

    private Integer avgHoldDay;

    private BigDecimal winRate;

    private BigDecimal avgProfitRate;

    private Integer holdNumber;

    private BigDecimal maxGain;

    private BigDecimal minGain;
}

package com.i000.stock.user.api.entity.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.BigDecimalRoundSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:50 2018/7/19
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetStatisticalBo {

    //    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal total;
    private BigDecimal todayGain;
    private BigDecimal totalGain;
    private BigDecimal totalGainRate;
}

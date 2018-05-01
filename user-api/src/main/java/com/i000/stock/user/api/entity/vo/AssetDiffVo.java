package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.BigDecimalRoundSerializer;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:04 2018/4/28
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDiffVo {

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal totalAmount;
    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal balance;
    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal stockAmount;
    private BigDecimal totalGain;
    private BigDecimal todayGain;
    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal initAmount;
    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal coverAmount;
}

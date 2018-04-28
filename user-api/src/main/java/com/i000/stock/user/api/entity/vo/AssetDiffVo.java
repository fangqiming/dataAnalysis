package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    private BigDecimal totalAmount;
    private BigDecimal balance;
    private BigDecimal stockAmount;
    private BigDecimal totalGain;
    private BigDecimal todayGain;
    private BigDecimal initAmount;
    private BigDecimal coverAmount;
}

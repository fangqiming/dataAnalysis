package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:15 2018/4/27
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeGainVo {
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private BigDecimal gainRate;
    private List<TradeVo> trade;
}

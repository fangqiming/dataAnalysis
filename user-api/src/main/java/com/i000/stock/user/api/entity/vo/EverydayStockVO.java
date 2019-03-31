package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EverydayStockVO {

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private String name;

    private String code;

    private BigDecimal oldPrice;

    private BigDecimal newPrice;

    private BigDecimal rate;

}

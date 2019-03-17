package com.i000.stock.user.api.entity.bo;

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
@NoArgsConstructor
@AllArgsConstructor
public class AIIndexBo {

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private BigDecimal score;

    private BigDecimal beat;

    private BigDecimal winRate;

    private BigDecimal minProfitRate;

    private BigDecimal maxProfitRate;

    private String result;
}

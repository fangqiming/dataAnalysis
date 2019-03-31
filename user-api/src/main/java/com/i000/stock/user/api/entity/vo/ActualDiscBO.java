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
@NoArgsConstructor
@AllArgsConstructor
public class ActualDiscBO {

    private String name;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate startDate;

    private BigDecimal marketCap;

    private BigDecimal initNetWorth;

    private BigDecimal netWorth;

    private BigDecimal withdrawal;
}

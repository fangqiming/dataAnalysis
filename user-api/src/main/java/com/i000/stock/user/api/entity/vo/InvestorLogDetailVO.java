package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvestorLogDetailVO {

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private String name;

    private String bankCard;

    private String type;

    private BigDecimal share;

    private BigDecimal amount;

    private String remark;

}

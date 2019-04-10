package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class RankVo {

    private String code;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private String name;

    /**
     * AI等分
     */
    private BigDecimal aiScore;

    /**
     * 雪球的URL
     */
    private String url;

    /**
     * 高管持股变化
     */
    private BigDecimal changeStock;
}

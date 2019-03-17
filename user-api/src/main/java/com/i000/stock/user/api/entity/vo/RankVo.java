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
     * 同花顺得分
     */
    private BigDecimal flushScore;
    /**
     * 综合得分
     */
    private BigDecimal totalScore;

    private BigDecimal marketCap;

    private BigDecimal grossProfitMargin;

    private BigDecimal peRatio;

    private BigDecimal debtRate;


}

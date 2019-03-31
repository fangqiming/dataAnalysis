package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class IndustryRankVO {

    private String industryName;

    private BigDecimal score;

    private Integer number;
}

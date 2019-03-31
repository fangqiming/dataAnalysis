package com.i000.stock.user.api.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RankBO {

    private String code;

    private String industry;

    private BigDecimal score;

}

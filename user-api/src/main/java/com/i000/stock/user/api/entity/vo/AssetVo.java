package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:38 2018/4/27
 * @Modified By:
 */
@Data
public class AssetVo {


    private Long id;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private BigDecimal stock;

    private BigDecimal balance;

    private BigDecimal cover;

    private BigDecimal gain;

    private String userCode;

    private BigDecimal totalGain;
}

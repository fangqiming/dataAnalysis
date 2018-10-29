package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:35 2018/10/27
 * @Modified By:
 */
@Data
public class ReverseRepoVO {
    

    private String code;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    private BigDecimal amount;

    private BigDecimal gain;

    private BigDecimal profit;

}

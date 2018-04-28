package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.api.jackson.TypeSerializer;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:57 2018/4/28
 * @Modified By:
 */
@Data
public class HoldVo {

    private Long id;
    private String name;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate oldDate;
    private BigDecimal oldPrice;
    private BigDecimal oldRank;
    private LocalDate newDate;
    private BigDecimal newPrice;
    private BigDecimal newRank;
    private Integer holdDay;
    private BigDecimal gain;

    @JsonSerialize(using = TypeSerializer.class)
    private String type;
}

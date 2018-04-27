package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:10 2018/4/27
 * @Modified By:
 */
@Data
public class TradeVo {

    private Long id;

    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private String type;
    private String name;
    private String action;
    private BigDecimal price;
    private String note;
}

package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.api.jackson.ActionSerializer;
import com.i000.stock.user.api.jackson.TypeSerializer;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:09 2018/5/3
 * @Modified By:
 */
@Data
public class TradeRecordVo {

    private Long id;
    @JsonSerialize(using = TypeSerializer.class)
    private String type;
    private String name;
    @JsonSerialize(using = ActionSerializer.class)
    private String action;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate oldDate;
    private BigDecimal oldPrice;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate newDate;
    private BigDecimal newPrice;
    private BigDecimal amount;
    private String userCode;
}

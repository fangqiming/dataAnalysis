package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class MaterialBO {
    private String name;
    private BigDecimal price;
    private BigDecimal priceBeforeWeek;
    private BigDecimal priceBeforeMonth;
    private String identifier;
    private String type;
}

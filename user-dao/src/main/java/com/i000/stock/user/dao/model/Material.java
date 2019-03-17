package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    private String name;
    private String spec;
    private BigDecimal price;
    private LocalDate date;
    private BigDecimal priceBeforeWeek;
    private LocalDate dateBeforeWeek;
    private BigDecimal priceBeforeMonth;
    private LocalDate dateBeforeMonth;
    private String identifier;
    private String type;
}

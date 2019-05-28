package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fundamentals {

    private String code;
    private String name;
    private BigDecimal sharpeRatio;
    private BigDecimal avgPe;
    private BigDecimal peg;
    private BigDecimal pe;
}

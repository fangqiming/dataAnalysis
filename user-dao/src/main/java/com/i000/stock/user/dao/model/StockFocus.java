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
public class StockFocus {
    private String date;
    private String code;
    private String name;

    private String changeStockUrl;
    private BigDecimal avgCost;
    private BigDecimal close;
    private BigDecimal amount;
    private String url;
    private BigDecimal aiScore;


    private Double day;
    private Double week;
    private Double volume;
    private Double price;
}

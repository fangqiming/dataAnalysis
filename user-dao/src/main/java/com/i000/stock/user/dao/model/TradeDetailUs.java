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
public class TradeDetailUs {

    private Long id;
    private String code;
    private String name;
    private LocalDate oldDate;
    private BigDecimal oldPrice;
    private LocalDate newDate;
    private BigDecimal newPrice;
    private String type;

}

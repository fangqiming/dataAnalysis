package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:44 2018/5/2
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeRecord {

    private Long id;
    private String type;
    private String name;
    private String action;
    private LocalDate oldDate;
    private BigDecimal oldPrice;
    private LocalDate newDate;
    private BigDecimal newPrice;

    private BigDecimal amount;
    private String userCode;
}

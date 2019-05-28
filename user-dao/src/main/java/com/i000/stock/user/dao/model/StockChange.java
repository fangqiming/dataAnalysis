package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
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
public class StockChange {

    @TableId
    private Long id;

    private LocalDate date;

    private String name;

    private String code;

    private BigDecimal changeNumber;

    private BigDecimal haveNumber;

    private BigDecimal tradePrice;

    private String occupation;

}

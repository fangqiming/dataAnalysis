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
public class FutureRecord {

    @TableId
    private Long id;

    private String code;

    private Integer amount;

    private BigDecimal shortMoney;

    private BigDecimal coverMoney;

    private Integer multiple;

    private LocalDate shortDate;

    private LocalDate coverDate;

    private String action;

    private String userCode;
}

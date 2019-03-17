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
@AllArgsConstructor
@NoArgsConstructor
public class AssetUs {
    @TableId
    private Long id;
    /**
     * 日期
     */
    private LocalDate date;
    /**
     * 持股金额
     */
    private BigDecimal stock;
    /**
     * 账户余额
     */
    private BigDecimal balance;

    private BigDecimal cover;
    /**
     * 用户码
     */
    private String user;
}

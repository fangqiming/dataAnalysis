package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 保存最新的股价信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPrice {

    @TableId
    private Long id;

    private String code;

    private BigDecimal close;
}

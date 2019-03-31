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
public class EverydayStock {

    @TableId
    private Long id;

    /**
     * 勾出日期
     */
    private LocalDate date;
    /**
     * 股票名称
     */
    private String name;

    /**
     * 股票代码
     */
    private String code;
    /**
     * 勾出价格
     */
    private BigDecimal oldPrice;
    /**
     * 最新价格
     */
    private BigDecimal newPrice;

}

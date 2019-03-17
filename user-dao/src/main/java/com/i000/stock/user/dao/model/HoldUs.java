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
public class HoldUs {

    @TableId
    private long id;
    /**
     * 股票代码
     */
    private String code;
    /**
     * 买入日期
     */
    private LocalDate oldDate;
    /**
     * 买入价格
     */
    private BigDecimal oldPrice;
    /**
     * 当前日期
     */
    private LocalDate newDate;
    /**
     * 当前价格
     */
    private BigDecimal newPrice;

    /**
     * 资产类型 做多，做空
     */
    private String type;
}

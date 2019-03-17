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
public class HoldNowUs {

    @TableId
    private Long id;
    /**
     * 股票代码
     */
    private String code;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 买入日期
     */
    private LocalDate oldDate;
    /**
     * 买入价格
     */
    private BigDecimal oldPrice;
    /**
     * 最新日期
     */
    private LocalDate newDate;
    /**
     * 最新价格
     */
    private BigDecimal newPrice;
    /**
     * 交易数量
     */
    private BigDecimal amount;
    /**
     * 持仓类型 做多，做空
     */
    private String type;
    /**
     * 用户码
     */
    private String user;
}

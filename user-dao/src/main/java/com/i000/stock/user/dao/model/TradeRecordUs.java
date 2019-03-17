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
public class TradeRecordUs {

    @TableId
    private Long id;
    /**
     * 公司名
     */
    private String name;
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
     * 平仓日期
     */
    private LocalDate newDate;
    /**
     * 平仓价格
     */
    private BigDecimal newPrice;
    /**
     * 交易数量
     */
    private BigDecimal amount;
    /**
     * 交易类型 空/多
     */
    private String type;
    /**
     * 用户名
     */
    private String user;
    /**
     * 动作
     */
    private String action;


}

package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.api.jackson.ActionSerializer;
import com.i000.stock.user.api.jackson.TypeSerializer;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:09 2018/5/3
 * @Modified By:
 */
@Data
public class TradeRecordVo {

    private Long id;

    /**
     * 交易日期
     */
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate tradeDate;
    /**
     * 股票代码
     */
    private String name;
    /**
     * 操作类型
     */
    @JsonSerialize(using = ActionSerializer.class)
    private String action;
    /**
     * 买入日期
     */
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate oldDate;
    /**
     * 买入价格
     */
    private BigDecimal oldPrice;
    @JsonSerialize(using = LocalDateSerializer.class)
    /**
     * 卖出日期
     */
    private LocalDate newDate;
    /**
     * 卖出价格
     */
    private BigDecimal newPrice;
    /**
     * 股票份数
     */
    private BigDecimal amount;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 盈亏率
     */
    private BigDecimal gainRate;


    /**
     * 盈亏绝对值
     */
    private BigDecimal gain;
}

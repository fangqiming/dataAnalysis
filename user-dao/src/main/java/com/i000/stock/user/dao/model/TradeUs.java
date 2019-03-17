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
public class TradeUs {

    @TableId
    private Long id;
    /**
     * 日期
     */
    private LocalDate date;
    /**
     * 类型 多/空
     */
    private String type;
    /**
     * 股票代码
     */
    private String code;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 交易动作
     */
    private String action;
    /**
     * 当前价格
     */
    private BigDecimal price;
    /**
     * 备注
     */
    private String note;
}

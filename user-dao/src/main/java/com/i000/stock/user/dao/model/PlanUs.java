package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlanUs {

    @TableId
    private Long id;
    /**
     * 推荐日期
     */
    private LocalDate date;
    /**
     * 股票代码
     */
    private String code;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 股票类型 空/多
     */
    private String type;
    /**
     * 操作类型
     */
    private String action;
    /**
     * 计划购买金额
     */
    private BigDecimal quantity;
    /**
     * 购买金额占总资产比例
     */
    private BigDecimal rate;
    /**
     * 备注
     */
    private String note;

}

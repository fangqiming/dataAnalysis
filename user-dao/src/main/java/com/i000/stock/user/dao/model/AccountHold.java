package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountHold {

    @TableId
    private Long id;
    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 账户名
     */
    private String accountName;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 成本价
     */
    private Double cost;

    /**
     * 当前价
     */
    private Double price;

}

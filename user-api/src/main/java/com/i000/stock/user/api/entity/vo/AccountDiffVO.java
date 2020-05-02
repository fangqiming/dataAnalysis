package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDiffVO {

    /**
     * 账户名
     */
    private String account;

    /**
     * 收益率
     */
    private Double gain;

    /**
     * 收益差
     */
    private Double gainDiff;

    /**
     * 平均仓位
     */
    private Double position;

    /**
     * 等仓位跑赢
     */
    private Double beta;

    /**
     * 最大回撤
     */
    private Double drawdown;

    /**
     * 最大回撤差
     */
    private Double drawdownDiff;

    /**
     * 最新的日期
     */
    private String date;

    /**
     * 总资产
     */
    private Double total;
}

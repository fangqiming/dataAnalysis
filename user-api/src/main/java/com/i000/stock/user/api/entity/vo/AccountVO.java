package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVO {

    /**
     * 账户名
     */
    private String name;

    /**
     * 净资产
     */
    private Double asset;

    /**
     * 净值
     */
    private Double net;

    /**
     * 收益率
     */
    private Double gain;

    /**
     * 收益率差
     */
    private Double gainDiff;

    /**
     * 仓位
     */
    private Double position;

    /**
     * 等仓位跑赢
     */
    private Double beta;

    /**
     * 日期
     */
    private String date;
}

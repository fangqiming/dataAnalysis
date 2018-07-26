package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description: 财务数据对象
 * @Date:Created in 13:50 2018/7/25
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Financial {
    /**
     * 应收票据占营收比
     */
    private BigDecimal billReceivableRate;
    /**
     * 股票代码
     */
    private String code;
    /**
     * 净利润环比增长率
     */
    private BigDecimal incNetProfitAnnual;
    /**
     * 负债率
     */
    private BigDecimal debtRatio;
    /**
     * 净利率
     */
    private BigDecimal netProfitMargin;
    /**
     * 静态PE
     */
    private BigDecimal staticPe;
    /**
     * 融资现金流净额
     */
    private BigDecimal netFinancing;
    /**
     * 应收账款占营收比
     */
    private BigDecimal accountReceivableRate;
    /**
     * 应付票据占营收比
     */
    private BigDecimal notesPayableRate;
    /**
     * 核心利润率
     */
    private BigDecimal coreProfitMargin;
    /**
     * ROA
     */
    private BigDecimal roa;
    /**
     * ROE
     */
    private BigDecimal roe;
    /**
     * 日期
     */
    private String day;
    /**
     * 投资现金净额
     */
    private BigDecimal netInvestment;
    /**
     * 营业收入
     */
    private BigDecimal operatingRevenue;
    /**
     * 市值
     */
    private BigDecimal marketCap;
    /**
     * 总资产周转率
     */
    private BigDecimal totalAssetsTurnoverRate;
    /**
     * 流动比率
     */
    private BigDecimal flowRatio;
    /**
     * 存货周转率
     */
    private BigDecimal inventoriesTurnoverRate;
    /**
     * 毛利率
     */
    private BigDecimal grossProfitMargin;
    /**
     * 经营现金流量净额
     */
    private BigDecimal netBusiness;
    /**
     * 流动资产周转率
     */
    private BigDecimal totalCurrentAssetsTurnoverRate;
    /**
     * 行业
     */
    private String industry;
    /**
     * 营收环比增长率
     */
    private BigDecimal incRevenueAnnual;
    /**
     * 动态PE
     */
    private BigDecimal dynamicPe;
    /**
     * 速动比率
     */
    private BigDecimal quickRatio;
    /**
     * 固定资产周转率
     */
    private BigDecimal fixedAssetsTurnoverRate;
    /**
     * 应付款项占营收比
     */
    private BigDecimal accountsPayableRate;
    /**
     * 应收款周转率
     */
    private BigDecimal accountReceivableTurnoverRate;

    /**
     * 总分
     */
    private BigDecimal total;
}

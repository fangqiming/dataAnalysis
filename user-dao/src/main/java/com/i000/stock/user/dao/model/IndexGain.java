package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description: 指数收益对象
 * @Date:Created in 14:20 2018/7/3
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexGain {

    @TableId
    private Long id;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 上证指数
     */
    private BigDecimal sz;

    /**
     * 沪深指数
     */
    private BigDecimal hs;

    /**
     * 创业板指数相对上一天的收益率
     */
    private BigDecimal cyb;

    /**
     * 上证指数相对上一天的收益率
     */
    private BigDecimal szGain;

    /**
     * 沪深指数相对上一天的收益率
     */
    private BigDecimal hsGain;

    /**
     * 创业板指数相对上一天的收益率
     */
    private BigDecimal cybGain;

    /**
     * 上证指数总收益率
     */
    private BigDecimal szTotal;

    /**
     * 沪深指数总收益率
     */
    private BigDecimal hsTotal;

    /**
     * 创业板指数总收益率
     */
    private BigDecimal cybTotal;

}

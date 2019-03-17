package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 同花顺诊断
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisFlush {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 诊断日期
     */
    private LocalDate date;


    /**
     * AI等分
     */
    private BigDecimal aiScore;
    /**
     * 同花顺得分
     */
    private BigDecimal flushScore;
    /**
     * 综合得分
     */
    private BigDecimal totalScore;


}

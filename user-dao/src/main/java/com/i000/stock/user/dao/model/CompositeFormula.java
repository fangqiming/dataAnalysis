package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description: 用于计算增长率的指标
 * @Date:Created in 14:51 2018/7/25
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompositeFormula {

    @TableId
    private Long id;

    private String expression;

    private BigDecimal maxValue;

    private String days;

    private String weight;
}

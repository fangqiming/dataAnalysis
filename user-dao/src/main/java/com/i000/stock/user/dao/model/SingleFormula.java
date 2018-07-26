package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description: 用于计算单一指标的公式  需要首先根据 日期获取，之后将结果保存到对象中去
 * @Date:Created in 14:53 2018/7/25
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleFormula {

    @TableId
    private Long id;

    private String expression;

    private BigDecimal maxValue;

    private String day;
}

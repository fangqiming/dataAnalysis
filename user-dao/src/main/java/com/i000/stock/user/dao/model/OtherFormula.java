package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description: 用于其他特殊公式的计算处理，如股权质押公式，分红公式等等
 * @Date:Created in 13:55 2018/7/26
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherFormula {

    @TableId
    private Long id;
    private String expression;
    private BigDecimal maxValue;
    private String tableName;

}

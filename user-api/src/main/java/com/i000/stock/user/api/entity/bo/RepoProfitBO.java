package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:37 2018/10/27
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepoProfitBO {

    private String code;

    /**
     * 购买额
     */
    private BigDecimal amount;

    /**
     * 收益
     */
    private BigDecimal profit;

    /**
     * 年化收益
     */
    private BigDecimal gian;
}

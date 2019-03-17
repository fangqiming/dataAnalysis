package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 20:21 2018/10/24
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelativeProfitBO {

    /**
     * 收益
     */
    private BigDecimal relativeProfit;
    /**
     * 收益率
     */
    private BigDecimal relativeProfitRate;
    /**
     * 跑赢指数
     */
    private BigDecimal beatStandardRate;
}

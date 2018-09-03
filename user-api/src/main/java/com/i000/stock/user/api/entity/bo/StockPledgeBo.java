package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:03 2018/8/18
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPledgeBo {
    private String code;
    private BigDecimal pledge;
}

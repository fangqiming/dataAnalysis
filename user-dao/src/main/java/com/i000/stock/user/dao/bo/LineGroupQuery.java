package com.i000.stock.user.dao.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:25 2018/4/27
 * @Modified By:
 */
@Data
public class LineGroupQuery {
    private String time;
    private BigDecimal baseMarket;
    private BigDecimal aiMarket;
    private BigDecimal gain;
}

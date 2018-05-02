package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:13 2018/5/2
 * @Modified By:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YieldRateVo {

    private List<BigDecimal> gain;
    private List<String> time;
}

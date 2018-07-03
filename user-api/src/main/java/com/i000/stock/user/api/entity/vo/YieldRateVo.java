package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public class YieldRateVo {

    /**
     * 千古指数获利
     */
    private List<BigDecimal> stockGain;

    /**
     * 上证的获利
     */
    private List<BigDecimal> szGain;

    /**
     * 沪深300的获利
     */
    private List<BigDecimal> hsGain;

    /**
     * 创业板的获利
     */
    private List<BigDecimal> cybGain;

    /**
     * 时间
     */
    private List<String> time;

    public YieldRateVo() {
        stockGain = new ArrayList<>(300);
        szGain = new ArrayList<>(300);
        hsGain = new ArrayList<>(300);
        cybGain = new ArrayList<>(300);
        time = new ArrayList<>(300);
    }
}

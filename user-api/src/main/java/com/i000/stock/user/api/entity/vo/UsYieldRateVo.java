package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class UsYieldRateVo {
    /**
     * 千古指数获利
     */
    private List<BigDecimal> stockGain;

    /**
     * 上证的获利
     */
    private List<BigDecimal> sp500Gain;

    /**
     * 沪深300的获利
     */
    private List<BigDecimal> djiGain;

    /**
     * 创业板的获利
     */
    private List<BigDecimal> nasdaqGain;


    /**
     * 时间
     */
    private List<String> time;

    public UsYieldRateVo() {
        stockGain = new ArrayList<>(300);
        sp500Gain = new ArrayList<>(300);
        djiGain = new ArrayList<>(300);
        nasdaqGain = new ArrayList<>(300);
        time = new ArrayList<>(300);
    }
}

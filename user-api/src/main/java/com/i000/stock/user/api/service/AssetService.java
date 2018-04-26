package com.i000.stock.user.api.service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:19 2018/4/26
 * @Modified By:
 */
public interface AssetService {

    /**
     * 计算保存用户资产
     *
     * @param date
     */
    void calculate(LocalDate date);


    /**
     * 计算某一天的资产收益率
     *
     * @param date
     * @return
     */
    BigDecimal getGain(LocalDate date);


    /**
     * 计算某个区间的资产收益率
     *
     * @param start
     * @param end
     * @return
     */
    BigDecimal getGain(LocalDate start, LocalDate end);
}

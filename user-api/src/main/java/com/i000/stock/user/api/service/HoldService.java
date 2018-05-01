package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.Hold;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:53 2018/4/28
 * @Modified By:
 */
public interface HoldService {

    /**
     * 获取用户的当前持股信息
     *
     * @return
     */
    List<Hold> findHold();


    /**
     * 更新当前持股的买入的股票份数
     * @param date
     * @param name
     * @param amount
     * @return
     */
    Integer updateAmount(LocalDate date, String name, BigDecimal amount);

    BigDecimal getAmount()
}

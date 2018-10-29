package com.i000.stock.user.api.service.original;

import com.i000.stock.user.dao.model.Hold;

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
     * 获取当天的交易详情
     *
     * @return
     */
    List<Hold> getTrade();

    /**
     * 当用户为空时将全部的持股当做交易来进行
     *
     * @param date
     * @return
     */
    List<Hold> findHoldInit(LocalDate date);

    LocalDate getMaxHold();

    Integer getHoldCount(LocalDate date);

    List<Hold> findByNameAndDate(LocalDate date, String name);




}

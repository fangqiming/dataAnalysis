package com.i000.stock.user.service.impl;


import com.i000.stock.user.api.service.original.TradeService;
import com.i000.stock.user.dao.mapper.TradeMapper;
import com.i000.stock.user.dao.model.Trade;
import com.i000.stock.user.dao.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;


/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:54 2018/4/27
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class TradeServiceImpl extends AbstractService<Trade, TradeMapper> implements TradeService {

    @Override
    public LocalDate getMaxDate() {
        return baseMapper.getMaxDate();
    }

    @Override
    public BigDecimal getSellPrice(String name) {
        return baseMapper.getPriceByName(name);
    }

    @Override
    public BigDecimal getCoverPrice(String name) {
        return baseMapper.getCoverPriceByName(name);
    }

    @Override
    public void updatePrice(String name, BigDecimal rate) {
        baseMapper.updatePrice(name, rate);
    }

    @Override
    public Integer getSellNum(LocalDate date) {
        return baseMapper.getSellNum(date);
    }

    @Override
    public Integer getBuyNum(LocalDate date) {
        return baseMapper.getBuyNum(date);
    }
}

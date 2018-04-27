package com.i000.stock.user.service.impl;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.service.TradeService;
import com.i000.stock.user.core.util.TimeUtil;
import com.i000.stock.user.dao.mapper.TradeMapper;
import com.i000.stock.user.dao.model.Trade;
import com.i000.stock.user.dao.service.AbstractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;


/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:54 2018/4/27
 * @Modified By:
 */
@Slf4j
@Component
public class TradeServiceImpl extends AbstractService<Trade, TradeMapper> implements TradeService {


    @Override
    public List<Trade> findByDate(LocalDate date) {
        return baseMapper.findByDate(date);
    }

    @Override
    public List<Trade> findByDate(List<LocalDate> dates) {
        List<String> date = TimeUtil.localData2StringList(dates);
        EntityWrapper<Trade> entityWrapper = createEntityWrapper();
        entityWrapper.in("date", date);
        return baseMapper.selectList(entityWrapper);
    }

    @Override
    public LocalDate getMaxDate() {
        return baseMapper.getMaxDate();
    }
}

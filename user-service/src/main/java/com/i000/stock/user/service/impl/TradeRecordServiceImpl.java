package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.TradeRecordService;
import com.i000.stock.user.dao.mapper.TradeRecordMapper;
import com.i000.stock.user.dao.model.TradeRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:54 2018/5/2
 * @Modified By:
 */
@Component
@Transactional
public class TradeRecordServiceImpl implements TradeRecordService {

    @Resource
    private TradeRecordMapper tradeRecordMapper;

    @Override
    public void save(TradeRecord tradeRecord) {
        tradeRecordMapper.insert(tradeRecord);
    }

    @Override
    public List<TradeRecord> find(LocalDate date, String userCode) {
        return tradeRecordMapper.find(date, userCode);
    }

    @Override
    public LocalDate getMaxDate(String userCode) {
        return tradeRecordMapper.getMaxDate(userCode);
    }
}

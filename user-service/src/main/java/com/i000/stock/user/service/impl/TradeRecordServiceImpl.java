package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.TradeRecordService;
import com.i000.stock.user.dao.mapper.TradeRecordMapper;
import com.i000.stock.user.dao.model.TradeRecord;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:54 2018/5/2
 * @Modified By:
 */
@Component
public class TradeRecordServiceImpl implements TradeRecordService {

    @Resource
    private TradeRecordMapper tradeRecordMapper;

    @Override
    public void save(TradeRecord tradeRecord) {
        tradeRecordMapper.insert(tradeRecord);
    }
}

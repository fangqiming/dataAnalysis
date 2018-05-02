package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.TradeRecord;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:53 2018/5/2
 * @Modified By:
 */
public interface TradeRecordService {
    /**
     * 保存交易记录
     *
     * @param tradeRecord
     * @return
     */
    void save(TradeRecord tradeRecord);
}

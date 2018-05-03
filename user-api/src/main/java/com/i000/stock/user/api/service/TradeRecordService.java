package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.TradeRecord;

import java.time.LocalDate;
import java.util.List;

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

    /**
     * 查找符合要求的交易记录
     *
     * @param date
     * @param userCode
     * @return
     */
    List<TradeRecord> find(LocalDate date, String userCode);
}

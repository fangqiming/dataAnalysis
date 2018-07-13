package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.api.entity.vo.TradeRecordVo;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
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

    /**
     * 查找指定userCode的最大日期
     *
     * @param userCode
     * @return
     */
    LocalDate getMaxDate(String userCode);

    /**
     * 分页查找交易记录
     *
     * @param baseSearchVo
     * @return
     */
    Page<TradeRecordVo> search(String userCode, BaseSearchVo baseSearchVo);

}

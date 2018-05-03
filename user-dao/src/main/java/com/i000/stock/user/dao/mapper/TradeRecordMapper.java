package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.TradeRecord;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:53 2018/5/2
 * @Modified By:
 */
public interface TradeRecordMapper extends BaseMapper<TradeRecord> {

    /**
     * 根据条件查找交易记录
     *
     * @param date
     * @param userCode
     * @return
     */
    List<TradeRecord> find(@Param("date") LocalDate date, @Param("userCode") String userCode);
}

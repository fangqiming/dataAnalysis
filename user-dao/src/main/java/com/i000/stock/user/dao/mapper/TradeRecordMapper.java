package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.TradeRecord;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.tomcat.jni.Local;

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

    /**
     * 查找指定userCode最新的交易日期
     *
     * @param userCode
     * @return
     */
    @Select("select max(new_date) from trade_record where user_code=#{userCode}")
    LocalDate getMaxDate(@Param("userCode") String userCode);


    @Select("select found_rows()")
    Long pageTotal();

    /**
     * 根据分页查询查询出符合要求的日期
     *
     * @param userCode
     * @param baseSearchVo
     * @return
     */
    @Select("select sql_calc_found_rows\n" +
            "        DISTINCT  old_date from trade_record  where user_code=#{userCode} ORDER BY old_date DESC\n" +
            "         limit ${baseSearchVo.start},${baseSearchVo.pageSize};")
    List<LocalDate> searchByDate(@Param("userCode") String userCode, @Param("baseSearchVo") BaseSearchVo baseSearchVo);


    List<TradeRecord> findTradeRecord(@Param("userCode") String userCode, @Param("dates") List<LocalDate> dates);
}

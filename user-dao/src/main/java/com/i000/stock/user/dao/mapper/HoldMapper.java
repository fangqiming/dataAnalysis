package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Hold;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:20 2018/4/26
 * @Modified By:
 */
public interface HoldMapper extends BaseMapper<Hold> {
    /**
     * 查出用户的某一天的持股情况
     *
     * @param date
     * @return
     */
    List<Hold> findByDate(@Param("date") LocalDate date);

    /**
     * 查询最大的发生过得交易
     *
     * @return
     */
    @Select("select max(new_date) from hold")
    LocalDate getMaxDate();

    /**
     * 更新当前持股的股票份数
     * @param date
     * @param name
     * @return
     */
    @Update("update hold set amount=${amount} where new_date=#{date} and old_date=#{date} and `name`=${name}")
    Integer updateAmount(LocalDate date, String name, BigDecimal amount);
}

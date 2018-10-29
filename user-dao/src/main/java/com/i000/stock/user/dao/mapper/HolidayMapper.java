package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Holiday;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:19 2018/10/26
 * @Modified By:
 */
public interface HolidayMapper extends BaseMapper<Holiday> {

    /**
     * 获取指定日期后20天的日期对象
     *
     * @param date
     * @return
     */
    List<Holiday> getTwentyDaysByDate(@Param("date") LocalDate date);
}

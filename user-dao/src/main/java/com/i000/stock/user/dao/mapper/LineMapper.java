package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.StepEnum;
import com.i000.stock.user.dao.bo.LineGroupQuery;
import com.i000.stock.user.dao.model.Line;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:20 2018/4/26
 * @Modified By:
 */
public interface LineMapper extends BaseMapper<Line> {

    /**
     * 查询全部的指数对比信息
     *
     * @return
     */
    List<LineGroupQuery> find();

    /**
     * 查询最大的日期
     *
     * @return
     */
    @Select("select max(date) from line")
    LocalDate getMaxDate();
}

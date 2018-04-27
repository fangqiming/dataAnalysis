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
     * 用于获取周/月的大盘走势图
     *
     * @param step 步长
     * @return
     */
    List<LineGroupQuery> findByStepDay(@Param("step") StepEnum step);

    /**
     * 用于获取 季/年的大盘走势图
     *
     * @param step 步长
     * @return
     */
    List<LineGroupQuery> findByStepGroup(@Param("step") StepEnum step);

    /**
     * 查询最大的发生过得交易
     *
     * @return
     */
    @Select("select max(date) from line")
    LocalDate getMaxDate();
}

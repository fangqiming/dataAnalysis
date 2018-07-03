package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.IndexGain;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description: 指数的存储与计算由定时器来完成
 * @Date:Created in 14:34 2018/7/3
 * @Modified By:
 */
public interface IndexGainMapper extends BaseMapper<IndexGain> {

    /**
     * 获取第一个指数记录
     *
     * @return
     */
    IndexGain getFirstIndexGain();

    /**
     * 获取库中最后一个指数记录
     *
     * @return
     */
    IndexGain getLastIndexGain();

    /**
     * 获取全部的指数记录
     *
     * @return
     */
    List<IndexGain> find();

    /**
     * 查询距离date 前diff天的记录
     *
     * @param date
     * @param diff
     * @return
     */
    IndexGain getDiff(@Param("date") LocalDate date, @Param("diff") Integer diff);
}

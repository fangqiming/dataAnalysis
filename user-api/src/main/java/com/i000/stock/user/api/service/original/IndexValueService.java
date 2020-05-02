package com.i000.stock.user.api.service.original;

import com.i000.stock.user.dao.model.IndexValue;
import net.sf.jsqlparser.statement.create.table.Index;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:09 2018/7/4
 * @Modified By:
 */
public interface IndexValueService {

    /**
     * 查找到全部的IndexValue信息
     * 此处用来根据指数信息生成指数的收益信息
     *
     * @return
     */
    List<IndexValue> findAll();

    /**
     * 查找出指定时间范围内的指数值
     *
     * @param start
     * @param end
     * @return
     */
    List<IndexValue> findBetween(LocalDate start, LocalDate end);

    /**
     * 获取距离指定日期最近的一条指数信息
     *
     * @param date
     * @return
     */
    IndexValue getRecently(LocalDate date);

    /**
     * 获取距离指定日期最近的一条指数信息
     *
     * @param date
     * @return
     */
    IndexValue getRecentlyByGt(LocalDate date);

    /**
     * 比如传递 2018-01-01 则获取到大于 2018-01-01的第一个交易日期指数信息
     *
     * @param date
     * @return
     */
    IndexValue getRecentlyByG(LocalDate date);

    /**
     * 比如传递 2018-01-01 则获取到小于 2018-01-01的第一个交易日期的指数信息
     *
     * @param date
     * @return
     */
    IndexValue getRecentlyByL(LocalDate date);

    IndexValue getRecentlyByLt(LocalDate date);

    /**
     * 获取表中最新的数据条目
     *
     * @return
     */
    IndexValue getLately();

    /**
     * 保存指数信息
     *
     * @param indexValue
     */
    void save(IndexValue indexValue);

    /**
     * 查询指定年的第一条记录
     *
     * @param year
     * @return
     */
    IndexValue getYearFirst(String year);

    /**
     * 获取最近的两条记录
     *
     * @return
     */
    List<IndexValue> getLatelyTwo();

    /**
     * 获取最早的记录
     *
     * @return
     */
    IndexValue getLastOne();

    IndexValue getBefore(LocalDate date);

    List<IndexValue> findByDates(List<LocalDate> list);


    IndexValue getByDate(LocalDate date);
}

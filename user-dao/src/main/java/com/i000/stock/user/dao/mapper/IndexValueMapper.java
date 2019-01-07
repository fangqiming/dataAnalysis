package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.IndexValue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:09 2018/7/4
 * @Modified By:
 */
public interface IndexValueMapper extends BaseMapper<IndexValue> {

    /**
     * 查询出指定日期范围内的指数值信息
     *
     * @param start
     * @param end
     * @return
     */
    @Select("select * from index_value where date BETWEEN #{start} and #{end}")
    List<IndexValue> findBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    /**
     * 查询距离指定日期最近的指数值信息
     *
     * @param date
     * @return
     */
    @Select("select * from index_value where date<=#{date} ORDER BY id DESC limit 1")
    IndexValue getLately(@Param("date") LocalDate date);

    @Select("select * from index_value where date>=#{date} ORDER BY id limit 1")
    IndexValue getLatelyByGt(@Param("date") LocalDate date);

    @Select("select * from index_value where date<=#{date} ORDER BY id DESC limit 1")
    IndexValue getLatelyByLt(@Param("date") LocalDate date);

    @Select("select * from index_value ORDER BY id DESC limit 1")
    IndexValue getNewest();

    @Select("select * from index_value where date>= #{year} ORDER BY id  limit 1")
    IndexValue getYearFirst(@Param("year") String year);

    @Select("select * from index_value ORDER BY id DESC limit 2")
    List<IndexValue> getLatelyTwo();

    @Select("select * from index_value ORDER BY id  limit 1")
    IndexValue getLastOne();

    @Select("select * from index_value where date <#{date}  ORDER BY date DESC limit 1")
    IndexValue getBefore(@Param("date") LocalDate date);
}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Access;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:36 2018/5/3
 * @Modified By:
 */
public interface AccessMapper extends BaseMapper<Access> {

    /**
     * 根据日期范围统计
     *
     * @param start
     * @param end
     * @return
     */
    @Select("select count(*) from access where  date>=#{start} and date<=#{end}")
    Integer getNumByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 根据日期范围外加城市统计
     *
     * @param start
     * @param end
     * @param city
     * @return
     */
    @Select("select count(*) from access where city=${city} and date>=#{start} and date<=#{end}")
    Integer getNumByCity(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("city") String city);

    /**
     * 根据日期范围外加国家统计
     *
     * @param start
     * @param end
     * @param country
     * @return
     */
    @Select("select count(*) from access where country=${country} and date>=#{start} and date<=#{end}")
    Integer getNumByCountry(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("country") String country);

    /**
     * 统计有哪些国家的人访问
     *
     * @return
     */
    @Select("select DISTINCT country from access")
    List<String> findCountry();

    /**
     * 统计有哪些城市的人访问
     *
     * @return
     */
    @Select("select DISTINCT city from access")
    List<String> findCity();
}

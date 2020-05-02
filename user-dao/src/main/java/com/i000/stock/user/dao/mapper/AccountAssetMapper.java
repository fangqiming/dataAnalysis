package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.AccountAssetBO;
import com.i000.stock.user.dao.model.AccountAsset;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface AccountAssetMapper extends BaseMapper<AccountAsset> {

    /**
     * 获取指定日期,国籍的当天的账户信息
     *
     * @param country
     * @param date
     * @return
     */
    List<AccountAssetBO> find(@Param("country") String country, @Param("date") LocalDate date);

    /**
     * 获取指定时间区间的全部账户信息
     *
     * @param start
     * @param end
     * @return
     */
    List<AccountAssetBO> findBetween(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("country") String country);

    /**
     * 获取指定国籍账户的小于指定日期的第一个日期
     *
     * @param country
     * @param date
     * @return
     */
    @Select("select DISTINCT date from account_asset where country =#{country} and date < #{date} ORDER BY date DESC limit 1")
    LocalDate getL(@Param("country") String country, @Param("date") LocalDate date);

    /**
     * 获取指定国籍账户的初始日期
     *
     * @param country
     * @return
     */
    @Select("select DISTINCT date from account_asset where country =#{country} ORDER BY date limit 1")
    LocalDate getInit(@Param("country") String country);

    /**
     * 获取指定国籍账户的最新日期
     *
     * @param country
     * @return
     */
    @Select("select max(date) from account_asset where country =#{country}")
    LocalDate getCurrent(@Param("country") String country);


}

package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.Access;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:37 2018/5/3
 * @Modified By:
 */
public interface AccessService {

    /**
     * 保存访问记录
     *
     * @param access
     */
    void save(Access access);

    /**
     * 查询某一天的访问量
     *
     * @param start
     * @param end
     * @return
     */
    Integer getNum(LocalDateTime start, LocalDateTime end);

    /**
     * 查询某个国家的访问量
     *
     * @param country
     * @return
     */
    Integer getNum(String country, LocalDateTime start, LocalDateTime end);

    /**
     * 查询某个城市的访问了量
     *
     * @param city
     * @return
     */
    Integer getNumByCity(String city, LocalDateTime start, LocalDateTime end);


    /**
     * 统计有哪些国家访问
     *
     * @return
     */
    List<String> findCountry();

    /**
     * 统计有哪些城市的人访问
     *
     * @return
     */
    List<String> findCity();
}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.Asset;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:21 2018/4/26
 * @Modified By:
 */
public interface AssetMapper extends BaseMapper<Asset> {

    /**
     * 获取最新的资产信息
     *
     * @return
     */
    Asset getLately(@Param("userCode") String userCode);

    /**
     * 获取距离最后一次交易间隔diff个时间的资产情况
     *
     * @param diff
     * @return
     */
    Asset getDiff(@Param("date") LocalDate date, @Param("diff") Integer diff, @Param("userCode") String userCode);

    /**
     * 查询全部符合条件的Asset信息
     *
     * @param date
     * @param diff
     * @param userCode
     * @return
     */
    List<Asset> findDiff(@Param("date") LocalDate date, @Param("diff") Integer diff, @Param("userCode") String userCode);

    /**
     * 获取指定日期的资产情况
     *
     * @param date
     * @return
     */
    Asset getByDate(@Param("date") LocalDate date, @Param("userCode") String userCode);


    /**
     * 分页查找资产信息情况
     *
     * @param baseSearchVo
     * @return
     */
    List<Asset> search(@Param("baseSearchVo") BaseSearchVo baseSearchVo, @Param("userCode") String userCode);

    /**
     * 查询分页条数
     *
     * @return
     */
    @Select("select found_rows()")
    Long pageTotal();

    /**
     * 获取平均资金的闲置率
     *
     * @param user
     * @return
     */
    @Select("select avg(balance/(balance+stock+cover)) from asset where user_code=#{user}")
    BigDecimal getAvgIdleRate(@Param("user") String user);

    /**
     * 获取当日的之间闲置率
     *
     * @param user
     * @return
     */
    @Select("select balance/(stock+balance+cover) from asset where id in (select max(id) from asset) and user_code=#{user}")
    BigDecimal getIdleRate(@Param("user") String user);


}

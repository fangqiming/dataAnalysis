package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.Asset;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
     * 新版的查询前多少天Asset记录
     *
     * @param userCode
     * @param date
     * @return
     */
    Asset getDiff_2(@Param("userCode") String userCode, @Param("date") LocalDate date);

    Asset getDiffByGt(@Param("userCode") String userCode, @Param("date") LocalDate date);

    Asset getDiffByLt(@Param("userCode") String userCode, @Param("date") LocalDate date);

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
    @Select("select balance/(stock+balance+cover) from asset where user_code=#{user} ORDER BY id DESC limit 1")
    BigDecimal getIdleRate(@Param("user") String user);


    /**
     * 根据日期和userCode查询满足条件的资金信息
     */
    List<Asset> findByDateUser(@Param("userCode") String userCode, @Param("dates") Set<LocalDate> dates);

    List<Asset> findBetween(@Param("userCode") String userCode, @Param("start") LocalDate start, @Param("end") LocalDate end);

    Asset getYearFirst(@Param("year") String year, @Param("userCode") String userCode);

    @Select("select gain from asset where user_code=#{userCode} and date >=  date_sub(curdate() , interval 30 day)  ORDER BY  gain DESC limit 1")
    BigDecimal getMaxGain(@Param("userCode") String userCode);

    @Select("select gain from asset where user_code=#{userCode} and date >=  date_sub(curdate() , interval 30 day)  ORDER BY gain limit 1")
    BigDecimal getMinGain(@Param("userCode") String userCode);

    List<Asset> getLatelyTwoByUserCode(@Param("userCode") String userCode);

    Asset getByUserCodeAndDate(@Param("userCode") String userCode, @Param("date") LocalDate date);

    Asset getBeforeDate(@Param("date") LocalDate date, @Param("userCode") String userCode);

    Asset getInit(@Param("userCode") String userCode);
}

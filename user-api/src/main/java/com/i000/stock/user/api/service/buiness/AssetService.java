package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.api.entity.vo.GainBo;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Hold;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:19 2018/4/26
 * @Modified By:
 */
public interface AssetService {

    /**
     * 获取最新的财务数据
     *
     * @return
     */
    Asset getLately(String userCode);


    /**
     * 获取到指定日期之前的前几天的资产信息
     *
     * @param date
     * @param day
     * @return
     */
    Asset getDiff(LocalDate date, Integer day, String userCode);

    /**
     * 对 getDiff(LocalDate date, Integer day, String userCode) 的重构版本
     *
     * @param date
     * @param userCode
     * @return
     */
    Asset getDiff(LocalDate date, String userCode);

    Asset getDiffByGt(LocalDate date, String userCode);

    /**
     * 查询一定范围内的全部资产信息
     *
     * @param date
     * @param day
     * @param userCode
     * @return
     */
    List<Asset> findDiff(LocalDate date, Integer day, String userCode);

    /**
     * 计算保存用户资产
     *
     * @param date
     */
    void calculate(LocalDate date, String userCode, List<Hold> trade, List<Hold> init);


    /**
     * 计算某个区间的资产收益率
     *
     * @param start
     * @param day
     * @return
     */
    GainBo getGain(LocalDate start, Integer day, String userCode);

    /**
     * 分页查找资产信息
     *
     * @param baseSearchVo
     * @return
     */
    Page<Asset> search(BaseSearchVo baseSearchVo, String userCode);

    /**
     * 获取平均资金的闲置率
     *
     * @param userCode
     * @return
     */
    BigDecimal getAvgIdleRate(String userCode);

    /**
     * 获取资金闲置率
     *
     * @param userCode
     * @return
     */
    BigDecimal getIdleRate(String userCode);

    /**
     * 获取指定用户指定事件范围内的收益信息
     *
     * @param start
     * @param end
     * @return
     */
    List<Asset> findAssetBetween(String userCode, LocalDate start, LocalDate end);

    /**
     * 查找某一年的第一条数据
     *
     * @param year
     * @param userCode
     * @return
     */
    Asset getYearFirst(String year, String userCode);

    /**
     * 获取最大单日盈利
     *
     * @param userCode
     * @return
     */
    BigDecimal getMaxGain(String userCode);

    /**
     * 获取最小单日盈利
     *
     * @param userCode
     * @return
     */
    BigDecimal getMinGain(String userCode);

    /**
     * 获取最新的两个资产对象
     *
     * @param userCode
     * @return
     */
    List<Asset> getLatelyTwoByUserCode(String userCode);

}

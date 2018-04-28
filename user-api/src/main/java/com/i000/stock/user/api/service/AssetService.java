package com.i000.stock.user.api.service;

import com.i000.stock.user.api.entity.vo.AssetDiffVo;
import com.i000.stock.user.api.entity.vo.GainBo;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.bo.Page;

import java.time.LocalDate;

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
    Asset getLately();

    /**
     * 获取到指定日期之前的前几天的资产信息
     *
     * @param date
     * @param day
     * @return
     */
    Asset getDiff(LocalDate date, Integer day);

    /**
     * 计算保存用户资产
     *
     * @param date
     */
    void calculate(LocalDate date);


    /**
     * 计算某个区间的资产收益率
     *
     * @param start
     * @param day
     * @return
     */
    GainBo getGain(LocalDate start, Integer day);

    /**
     * 分页查找资产信息
     *
     * @param baseSearchVo
     * @return
     */
    Page<Asset> search(BaseSearchVo baseSearchVo);

    /**
     * 获取资产的总体信息
     *
     * @return
     */
    AssetDiffVo getSummary();
}

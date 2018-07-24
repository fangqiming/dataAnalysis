package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.api.entity.vo.PageGainVo;
import com.i000.stock.user.api.entity.vo.YieldRateVo;

import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:02 2018/7/11
 * @Modified By:
 */
public interface GainRateService {

    /**
     * 获取自定用户的价格指数走势  即首页的各种指数的走势对比折线图
     *
     * @param userCode 用户码
     * @param diff     间隔
     * @param end      结束日期   开始日期=end-diff
     * @return
     */
    YieldRateVo getIndexTrend(String userCode, Integer diff, LocalDate end);

    /**
     * 获取最近一段时间内的收益信息
     *
     * @param userCode 用户码
     * @param diff     间隔
     * @param end      结束日期   开始日期=end-diff
     * @param title    首页显示的名称
     * @return
     */
    PageGainVo getRecentlyGain(String userCode, Integer diff, LocalDate end, String title);

    /**
     * 获取预期年化收益
     *
     * @param pageGainVo
     * @param date
     * @return
     */
    PageGainVo getYearRate(PageGainVo pageGainVo, LocalDate date);

    /**
     * 获取今年以来的收益信息
     *
     * @param userCode
     * @param diff
     * @param end
     * @param title
     * @return
     */
    PageGainVo getFromYearStart(String userCode, Integer diff, LocalDate end, String title);

}
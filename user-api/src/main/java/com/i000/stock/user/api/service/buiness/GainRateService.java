package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.api.entity.bo.RelativeProfitBO;
import com.i000.stock.user.api.entity.vo.HistoryProfitVO;
import com.i000.stock.user.api.entity.vo.PageGainVo;
import com.i000.stock.user.api.entity.vo.YieldRateVo;

import java.math.BigDecimal;
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
     * @param date     间隔
     * @param end      结束日期   开始日期=end-diff
     * @return
     */
    YieldRateVo getIndexTrend(String userCode, LocalDate date, LocalDate end);

    /**
     * 获取最近一段时间内的收益信息
     *
     * @param userCode 用户码
     * @param start    间隔
     * @param end      结束日期   开始日期=end-diff
     * @param title    首页显示的名称
     * @return
     */
    PageGainVo getRecentlyGain(String userCode, LocalDate start, String title);

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


    /**
     * 获取指定用户当天跑赢上证的比率
     *
     * @param userCode
     * @return
     */
    RelativeProfitBO getTodayBeatSzByUserCode(String userCode);

    /**
     * 获取指定用户累计跑赢上证多少
     *
     * @param userCode
     * @return
     */
    RelativeProfitBO getTotalBeatByUserCode(String userCode);

    /**
     * 计算回撤
     *
     * @param user
     * @param diff
     * @return
     */
    BigDecimal getWithdrawal(String user, Integer diff);

    /**
     * 计算指定日期之间的收益率,包含跑赢上证的收益率
     * 注意比如传递 start 2012-01-01  end 2012-02-01 则实际的开始日期日2012-01-01前的第一个交易日期
     *
     * @param start
     * @param end
     * @return
     */
    HistoryProfitVO getHistory(LocalDate start, LocalDate end, String title);

    /**
     * 计算年化收益
     *
     * @return
     */
    HistoryProfitVO getYearRate(LocalDate end);


}

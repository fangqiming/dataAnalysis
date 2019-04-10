package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.AccountSummaryVo;
import com.i000.stock.user.api.entity.bo.RelativeProfitBO;
import com.i000.stock.user.api.entity.bo.TodayAccountBo;
import com.i000.stock.user.api.entity.bo.TotalAccountBo;
import com.i000.stock.user.api.entity.constant.AuthEnum;
import com.i000.stock.user.api.entity.vo.*;
import com.i000.stock.user.api.service.buiness.UserLoginService;
import com.i000.stock.user.core.constant.enums.TimeZoneEnum;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.TimeUtil;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.ActualDiscService;
import com.i000.stock.user.service.impl.us.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@RequestMapping("/trade_us")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TradeUsController {


    @Autowired
    private UserInfoUsService userInfoUsService;

    @Autowired
    private AssetUsService assetUsService;

    @Autowired
    private UsGainRateService usGainRateService;

    @Autowired
    private TradeRecordUsService tradeRecordUsService;

    @Autowired
    private HoldNowUsService holdNowUsService;

    @Autowired
    private PlanUsService planUsService;

    @Autowired
    private ActualDiscService actualDiscService;

    @Autowired
    private UserLoginService userLoginService;

    /**
     * 127.0.0.1:8081//trade/get_asset_summary
     * 账户总览
     * 经过测试基本可用  数据的正确性需要验证
     */
    @GetMapping(path = "/get_asset_summary")
    public ResultEntity getAssetSummary() {
        String user = getUserCode();
        UserInfoUs userInfo = userInfoUsService.getByUser(user);
        AssetUs now = assetUsService.getNewest(user);
        TodayAccountBo todayAccountBo;
        TotalAccountBo totalAccountBo;
        if (Objects.isNull(now)) {
            LocalDate date = TimeUtil.getDateTimeByTimeZone(TimeZoneEnum.NEW_YORK).toLocalDate();
            todayAccountBo = TodayAccountBo.builder().date(date.format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                    .totalAsset(BigDecimal.ZERO)
                    .relativeProfit(BigDecimal.ZERO)
                    .relativeProfitRate(BigDecimal.ZERO)
                    .beatStandardRate(BigDecimal.ZERO)
                    .position(BigDecimal.ZERO)
                    .stockMarket(BigDecimal.ZERO)
                    .cover(BigDecimal.ZERO)
                    .balance(BigDecimal.ZERO).build();
            totalAccountBo = TotalAccountBo.builder()
                    .date(userInfo.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                    .initAmount(userInfo.getAmount())
                    .relativeProfit(BigDecimal.ZERO)
                    .relativeProfitRate(BigDecimal.ZERO)
                    .beatStandardRate(BigDecimal.ZERO)
                    //平均仓位的计算方式
                    .avgPosition(BigDecimal.ZERO)
                    .shortRate(BigDecimal.ZERO)
                    //最大回撤
                    .maxWithdrawal(BigDecimal.ZERO)
                    .repoProfit(BigDecimal.ZERO)
                    .build();

        } else {
            RelativeProfitBO todayBeatSzByUserCode = usGainRateService.getTodayBeatSzByUserCode(user);
            todayAccountBo = TodayAccountBo.builder().date(now.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                    .totalAsset(now.getBalance().add(now.getStock()).add(now.getCover()))
                    .relativeProfit(todayBeatSzByUserCode.getRelativeProfit())
                    .relativeProfitRate(todayBeatSzByUserCode.getRelativeProfitRate())
                    .beatStandardRate(todayBeatSzByUserCode.getBeatStandardRate())
                    .position(usGainRateService.getPosition(now))
                    .stockMarket(now.getStock())
                    .balance(now.getBalance())
                    .cover(now.getCover().abs()).build();
            RelativeProfitBO totalBeatByUserCode = usGainRateService.getTotalBeatByUserCode(user);
            totalAccountBo = TotalAccountBo.builder()
                    .date(userInfo.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                    .initAmount(userInfo.getAmount())
                    .relativeProfit(totalBeatByUserCode.getRelativeProfit())
                    .relativeProfitRate(totalBeatByUserCode.getRelativeProfitRate())
                    .beatStandardRate(totalBeatByUserCode.getBeatStandardRate())
                    //平均仓位的计算方式
                    .avgPosition(assetUsService.getAvgPositionByUser(user))
                    //最大回撤
                    .maxWithdrawal(usGainRateService.getWithdrawal(user, 90))
                    .repoProfit(BigDecimal.ZERO)
                    .shortRate(tradeRecordUsService.getShortWinRate(user))
                    .build();
        }

        AccountSummaryVo result = new AccountSummaryVo();
        OperatorUsVO operatorSummary = tradeRecordUsService.getOperatorInfo(user);
        result.setTodayAccountBo(todayAccountBo);
        result.setTotalAccountBo(totalAccountBo);
        result.setOperatorUsVO(operatorSummary);
        return Results.newSingleResultEntity(result);
    }

    /**
     * 获取业绩总览
     *
     * @param data
     * @return
     */
    @GetMapping(path = "/find_gain")
    public ResultEntity findProfit(@RequestParam(defaultValue = "") String data) {
        LocalDate temp = StringUtils.isEmpty(data) ?
                TimeUtil.getDateTimeByTimeZone(TimeZoneEnum.NEW_YORK).toLocalDate() :
                LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String userCode = getUserCode();
        List<PageGainVo> result = new ArrayList<>(4);
        result.add(usGainRateService.getRecentlyGain(userCode, temp.minusDays(7), "近一周"));
        result.add(usGainRateService.getRecentlyGain(userCode, temp.minusMonths(1), "近一月"));
        result.add(usGainRateService.getRecentlyGain(userCode, temp.minusMonths(3), "近一季"));
        result.add(usGainRateService.getRecentlyGain(userCode, temp.minusMonths(12), "近一年"));
        return Results.newListResultEntity(result);
    }

    /**
     * 美股折线图
     *
     * @param diff
     * @return
     */
    @GetMapping(path = "/get_gain_contrast")
    public ResultEntity getContrast(@RequestParam(defaultValue = "365") Integer diff) {
        String userCode = getUserCode();
        LocalDate date = getDate(diff);
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        UsYieldRateVo result = usGainRateService.getIndexTrend(userCode, date, LocalDate.now());
        return Results.newSingleResultEntity(result);
    }

    private LocalDate getDate(Integer diff) {
        LocalDate date = TimeUtil.getDateTimeByTimeZone(TimeZoneEnum.NEW_YORK).toLocalDate();
        //此处为月或者季
        if (diff > 0 && diff % 30 == 0) {
            Integer month = diff / 30;
            return date.minusMonths(month);
        }
        //为周，或者以天数计
        if (diff > 0) {
            return date.minusDays(diff);
        }
        //今年以来
        if (diff == 0) {
            String year = date.format(DateTimeFormatter.ofPattern("yy"));
            String dateStr = year + "-01-01";
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yy-MM-dd"));
        }
        //出错，或者其它均是上线以来
        return LocalDate.parse("19-02-01", DateTimeFormatter.ofPattern("yy-MM-dd"));
    }

    /**
     * 获取交易统计的接口
     *
     * @return
     */
    @GetMapping(path = "/get_operator_summary")
    public ResultEntity getOperator() {
        String userCode = getUserCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        OperatorUsVO operatorSummary = tradeRecordUsService.getOperatorInfo(userCode);
        return Results.newSingleResultEntity(operatorSummary);
    }

    /**
     * 127.0.0.1:8081/trade/find_stock
     * 获取当前持仓
     *
     * @return
     */
    @GetMapping(path = "/find_stock")
    public ResultEntity findHoldStock() {
        String userCode = getUserCode();
        String accessCode = getAccessCode();
        userLoginService.checkAuth(accessCode, AuthEnum.US_STOCK);

        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        List<HoldNowUs> hold = holdNowUsService.findByUser(userCode);
        if (!CollectionUtils.isEmpty(hold)) {
            //开始做转化了
            List<HoldNowUs> collect = hold.stream().filter(a -> a.getAmount().compareTo(BigDecimal.ZERO) > 0).collect(toList());
            List<HoldNowVo> holdNowVos = ConvertUtils.listConvert(collect, HoldNowVo.class, (d, s) -> {
                d.setAmount(s.getAmount().intValue());
                d.setName(s.getCode());
                d.setStockName(s.getName());
                d.setGain((s.getNewPrice().subtract(s.getOldPrice())).divide(s.getOldPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
            });
            for (HoldNowVo holdNowVo : holdNowVos) {
                //成本
                holdNowVo.setCost(holdNowVo.getOldPrice().multiply(new BigDecimal(holdNowVo.getAmount())));
                //目前的市值
                holdNowVo.setValue(holdNowVo.getNewPrice().multiply(new BigDecimal(holdNowVo.getAmount())));
                holdNowVo.setEarning(holdNowVo.getValue().subtract(holdNowVo.getCost()));
                if ("SHORT".equals(holdNowVo.getType())) {
                    //赚的金额
                    holdNowVo.setEarning(holdNowVo.getEarning().multiply(new BigDecimal(-1)));
                    //盈亏率
                    holdNowVo.setGain(holdNowVo.getGain().multiply(new BigDecimal(-1)));
                }
            }
            return Results.newListResultEntity(holdNowVos);
        }
        return Results.newListResultEntity(new ArrayList<>(0));
    }

    /**
     * 获取最新推荐
     *
     * @return
     */
    @GetMapping(path = "/find_plan")
    public ResultEntity findPlan() {
        String accessCode = getAccessCode();
        userLoginService.checkAuth(accessCode, AuthEnum.US_STOCK);
        List<PlanUs> recommend = planUsService.findRecommend();
        if (!CollectionUtils.isEmpty(recommend)) {
            List<PlanVo> result = ConvertUtils.listConvert(recommend, PlanVo.class, (d, s) -> {
                d.setStockName(s.getName());
                d.setName(s.getCode());
                d.setNewDate(s.getDate());
                d.setAmount(s.getQuantity());
                d.setInvestmentRatio(s.getRate());
            });
            return Results.newListResultEntity(result);
        }
        return Results.newListResultEntity(new ArrayList<>(0));
    }

    /**
     * 获取交易记录
     *
     * @return
     */
    @GetMapping(path = "/search_trade")
    public ResultEntity searchTrade(BaseSearchVo baseSearchVo) {
        ValidationUtils.validate(baseSearchVo);
        String userCode = getUserCode();
        //
        PageResult<TradeRecordUs> tradeRecords = tradeRecordUsService.search(baseSearchVo, userCode);
        List<TradeRecordUs> trades = tradeRecords.getList();
        List<TradeRecordVo> result = ConvertUtils.listConvert(trades, TradeRecordVo.class, (d, s) -> {
            d.setName(s.getCode());
            d.setCompanyName(s.getName());
            d.setTradeDate(s.getOldDate());
            d.setGain((s.getNewPrice().subtract(s.getOldPrice())).multiply(s.getAmount()));
            d.setGainRate((s.getNewPrice().subtract(s.getOldPrice())).divide(s.getOldPrice(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)));
            if ("COVER".equals(s.getAction())) {
                d.setGainRate(d.getGainRate().multiply(new BigDecimal(-1)));
                d.setGain(d.getGain().multiply(new BigDecimal(-1)));
            }
        });
        return CollectionUtils.isEmpty(trades)
                ? Results.newPageResultEntity(0L, new ArrayList<>(0))
                : Results.newPageResultEntity(tradeRecords.getTotal(), result);
    }

    /**
     * 查看实盘列表
     *
     * @param type
     * @return
     */
    @GetMapping(path = "/find_actual_list")
    public ResultEntity findActual(@RequestParam String type) {
        ValidationUtils.validateStringParameter(type, "类型不能为空");
        ActualDiscVO actual = actualDiscService.findActual(type);
        return Results.newSingleResultEntity(actual);
    }

    /**
     * 查看实盘详情
     *
     * @param name
     * @param baseSearchVo
     * @return
     */
    @GetMapping(path = "/search_actual_detail")
    public ResultEntity findActualDetail(String name, BaseSearchVo baseSearchVo) {
        ValidationUtils.validateStringParameter(name, "名称不能为空");
        ActualDiscDetailVO detail = actualDiscService.getDetailByName(name, baseSearchVo);
        return Results.newSingleResultEntity(detail);
    }

    @GetMapping(path = "/get_actual_line")
    public ResultEntity findActualLine(@RequestParam String name) {
        ValidationUtils.validateStringParameter(name, "名称不能为空");
        BaseLineTrendVO trend = actualDiscService.getTrendByName(name);
        return Results.newSingleResultEntity(trend);
    }

    private String getUserCode() {
        RequestContext instance = RequestContext.getInstance();
        if (Objects.nonNull(instance)) {
            String userCode = instance.getAmountShare();
            return StringUtils.isEmpty(userCode) ? "10000000" : userCode;
        }
        return "10000000";
    }

    private String getAccessCode() {
        RequestContext instance = RequestContext.getInstance();
        if (Objects.nonNull(instance)) {
            String accessCode = instance.getAccessCode();
            return StringUtils.isEmpty(accessCode) ? "NOT" : accessCode;
        }
        return "NOT";
    }

}

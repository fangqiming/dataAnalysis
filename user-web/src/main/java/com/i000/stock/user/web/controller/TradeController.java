package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.AccountSummaryVo;
import com.i000.stock.user.api.entity.bo.RelativeProfitBO;
import com.i000.stock.user.api.entity.bo.TodayAccountBo;
import com.i000.stock.user.api.entity.bo.TotalAccountBo;
import com.i000.stock.user.api.entity.constant.AuthEnum;
import com.i000.stock.user.api.entity.vo.*;
import com.i000.stock.user.api.service.buiness.*;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.api.service.original.LineService;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.LineGroupQuery;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.HoldNow;
import com.i000.stock.user.dao.model.ReverseRepo;
import com.i000.stock.user.dao.model.UserInfo;
import com.i000.stock.user.service.impl.ReverseRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:30 2018/4/27
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/trade")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TradeController {

    @Resource
    private AssetService assetService;

    @Resource
    private HoldNowService holdNowService;

    @Resource
    private LineService lineService;

    @Resource
    private UserInfoService userInfoService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private OperateSummaryService operateSummaryService;

    @Autowired
    private TradeRecordService tradeRecordService;

    @Autowired
    private GainRateService gainRateService;

    @Autowired
    private ReverseRepoService reverseRepoService;

    @Autowired
    private UserLoginService userLoginService;

    /**
     * 127.0.0.1:8081/trade/find_gain
     * 获取首页的最近获利情况描述
     * todo  首页的 userCode 还是应该为 echo_gou
     *
     * @return
     */
    @GetMapping(path = "/find_gain")
    public ResultEntity findProfit(@RequestParam(defaultValue = "") String data) {
        LocalDate temp = StringUtils.isEmpty(data) ? LocalDate.now() : LocalDate.parse(data, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String userCode = getUserCode();
        List<PageGainVo> result = new ArrayList<>(4);
        result.add(gainRateService.getRecentlyGain(userCode, temp.minusDays(7), "近一周"));

        result.add(gainRateService.getRecentlyGain(userCode, temp.minusMonths(1), "近一月"));
        result.add(gainRateService.getRecentlyGain(userCode, temp.minusMonths(3), "近一季"));
        result.add(gainRateService.getRecentlyGain(userCode, temp.minusMonths(12), "近一年"));
        return Results.newListResultEntity(result);
    }

    /**
     * 127.0.0.1:8081/trade/get_gain_contrast
     * 获取首页各种指数收益的折线对比  网站首页接口需要更改
     * 近一周 7   近一月 30  近一季 90  今年以来 0  上线以来 -1
     *
     * @return
     */
    @GetMapping(path = "/get_gain_contrast")
    public ResultEntity getContrast(@RequestParam(defaultValue = "365") Integer diff) {
        String userCode = getUserCode();
        LocalDate date = getDate(diff);
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        YieldRateVo result = gainRateService.getIndexTrend(userCode, date, LocalDate.now());
        return Results.newSingleResultEntity(result);
    }

    private LocalDate getDate(Integer diff) {
        //此处为月或者季
        if (diff > 0 && diff % 30 == 0) {
            Integer month = diff / 30;
            return LocalDate.now().minusMonths(month);
        }
        //为周，或者以天数计
        if (diff > 0) {
            return LocalDate.now().minusDays(diff);
        }
        //今年以来
        if (diff == 0) {
            String year = LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
            String dateStr = year + "-01-01";
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yy-MM-dd"));
        }
        //出错，或者其它均是上线以来
        return LocalDate.parse("18-01-01", DateTimeFormatter.ofPattern("yy-MM-dd"));
    }

    /**
     * 127.0.0.1:8082/recommend/get_index_contrast
     * 此处就也需要修改了
     *
     * @return
     */
    @GetMapping(path = "/get_index_contrast")
    public ResultEntity getBaseLineTrend() {
        List<LineGroupQuery> baseLines = lineService.find();
        BaseLineTrendVO baseLineTrendVO = new BaseLineTrendVO();
        baseLines.stream().sorted(Comparator.comparing(LineGroupQuery::getTime)).forEach(baseLine -> {
            baseLineTrendVO.getAiMarket().add(baseLine.getAiMarket().divide(new BigDecimal(10), 0, BigDecimal.ROUND_HALF_UP));
            baseLineTrendVO.getBaseMarket().add(baseLine.getBaseMarket().divide(new BigDecimal(10), 0, BigDecimal.ROUND_HALF_UP));
            baseLineTrendVO.getTime().add(baseLine.getTime().substring(2, 10));
        });
        return Results.newSingleResultEntity(baseLineTrendVO);
    }

    /**
     * 127.0.0.1:8081//trade/get_asset_summary
     * 获取首页的账户总览信息
     * 经过测试基本可用  数据的正确性需要验证
     */
    @GetMapping(path = "/get_asset_summary")
    public ResultEntity getAssetSummary() {
        String userCode = getUserCode();
        UserInfo userInfo = userInfoService.getByName(userCode);
        Asset now = assetService.getLately(userCode);
        RelativeProfitBO todayBeatSzByUserCode = gainRateService.getTodayBeatSzByUserCode(userCode);
        TodayAccountBo todayAccountBo = TodayAccountBo.builder().date(now.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                .totalAsset(now.getBalance().add(now.getStock()).add(now.getCover()))
                .relativeProfit(todayBeatSzByUserCode.getRelativeProfit())
                .relativeProfitRate(todayBeatSzByUserCode.getRelativeProfitRate())
                .beatStandardRate(todayBeatSzByUserCode.getBeatStandardRate())
                .position(getPosition(now))
                .stockMarket(now.getStock())
                .balance(now.getBalance()).build();

        RelativeProfitBO totalBeatByUserCode = gainRateService.getTotalBeatByUserCode(userCode);
        TotalAccountBo totalAccountBo = TotalAccountBo.builder()
                .date(userInfo.getCreatedTime().toLocalDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                .initAmount(userInfo.getInitAmount())
                .relativeProfit(totalBeatByUserCode.getRelativeProfit())
                .relativeProfitRate(totalBeatByUserCode.getRelativeProfitRate())
                .beatStandardRate(totalBeatByUserCode.getBeatStandardRate())
                //平均仓位的计算方式
                .avgPosition((BigDecimal.ONE.subtract(assetService.getAvgIdleRate(userCode))).multiply(new BigDecimal(100)))
                .repoProfit(now.getTotalRepoProfit())
                .maxWithdrawal(gainRateService.getWithdrawal(userCode, 90))
                .repoProfitRate(BigDecimal.ZERO)
                .build();
        if (now.getTotalRepoAmount().compareTo(BigDecimal.ZERO) > 0) {
            //
            BigDecimal total = now.getStock().add(now.getBalance()).add(now.getCover());
            BigDecimal profitRate =
                    now.getTotalRepoProfit().divide(total, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
            totalAccountBo.setRepoProfitRate(profitRate);
        }
        AccountSummaryVo result = new AccountSummaryVo();
        OperatorVo operatorSummary = operateSummaryService.getOperatorSummary(userCode);
        result.setTodayAccountBo(todayAccountBo);
        result.setTotalAccountBo(totalAccountBo);
        result.setOperatorVo(operatorSummary);
        return Results.newSingleResultEntity(result);
    }

    private BigDecimal getPosition(Asset asset) {
        return asset.getStock()
                .divide((asset.getStock().add(asset.getCover()).add(asset.getBalance())), 4, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(100));
    }

    /**
     * 127.0.0.1:8081/trade/find_stock
     * 首页获取当前的持仓信息  基本通过测试
     *
     * @return
     */
    @GetMapping(path = "/find_stock")
    public ResultEntity findHoldStock() {
        String userCode = getUserCode();
        String accessCode = getAccessCode();
        userLoginService.checkAuth(accessCode, AuthEnum.A_STOCK);
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        List<HoldNow> hold = holdNowService.find(userCode);
        if (!CollectionUtils.isEmpty(hold)) {
            List<HoldNow> collect = hold.stream().filter(a -> a.getAmount() > 0).collect(toList());
            List<HoldNowVo> holdNowVos = ConvertUtils.listConvert(collect, HoldNowVo.class);
            for (HoldNowVo holdNowVo : holdNowVos) {
                holdNowVo.setCost(holdNowVo.getOldPrice().multiply(new BigDecimal(holdNowVo.getAmount())));
                holdNowVo.setValue(holdNowVo.getNewPrice().multiply(new BigDecimal(holdNowVo.getAmount())));
                holdNowVo.setEarning(holdNowVo.getValue().subtract(holdNowVo.getCost()));
                holdNowVo.setStockName(companyService.getNameByCode(holdNowVo.getName()));
                holdNowVo.setGain(holdNowVo.getGain().multiply(new BigDecimal(100)));
            }
            return Results.newListResultEntity(holdNowVos);
        }
        return Results.newListResultEntity(new ArrayList<>(0));
    }

    /**
     * 获取操作统计的接口
     *
     * @return
     */
    @GetMapping(path = "/get_operator_summary")
    public ResultEntity getOperator() {
        String userCode = getUserCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        OperatorVo operatorSummary = operateSummaryService.getOperatorSummary(userCode);
        return Results.newSingleResultEntity(operatorSummary);
    }

    /**
     * 需要获取每天的交易记录分页获取交易详情的接口
     */
    @GetMapping(path = "/search_trade")
    public ResultEntity searchTrade(BaseSearchVo baseSearchVo) {
        ValidationUtils.validate(baseSearchVo);
        String userCode = getUserCode();
        PageResult<TradeRecordVo> result = tradeRecordService.search(userCode, baseSearchVo);
        return CollectionUtils.isEmpty(result.getList())
                ? Results.newPageResultEntity(0L, new ArrayList<>(0))
                : Results.newPageResultEntity(result.getTotal(), result.getList());
    }

    @GetMapping(path = "/search_reverse_repo")
    public ResultEntity searchRepo(BaseSearchVo baseSearchVo) {
        ValidationUtils.validate(baseSearchVo);
        String userCode = getUserCode();
        PageResult<ReverseRepo> search = reverseRepoService.search(userCode, baseSearchVo);

        if (search.getTotal() == 0) {
            return Results.newPageResultEntity(0L, new ArrayList<>(0));
        }
        List<ReverseRepoVO> data = ConvertUtils.listConvert(search.getList(), ReverseRepoVO.class);
        return Results.newPageResultEntity(search.getTotal(), data);
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

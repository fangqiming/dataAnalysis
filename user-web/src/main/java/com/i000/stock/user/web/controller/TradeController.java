package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.*;
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
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.ReverseRepoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
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

    /**
     * 127.0.0.1:8081/trade/find_gain
     * 获取首页的最近获利情况描述
     * todo  首页的 userCode 还是应该为 echo_gou
     *
     * @return
     */
    @GetMapping(path = "/find_gain")
    public ResultEntity findProfit() {
        String userCode = getUserCode();
        Asset asset = assetService.getLately(userCode);
        List<PageGainVo> result = new ArrayList<>(4);
        result.add(gainRateService.getRecentlyGain(userCode, 30, LocalDate.now(), "近一月"));
        result.add(gainRateService.getRecentlyGain(userCode, 60, LocalDate.now(), "近二月"));
        result.add(gainRateService.getRecentlyGain(userCode, 90, LocalDate.now(), "近三月"));
        PageGainVo fromYear = gainRateService.getFromYearStart(userCode, 370, asset.getDate(), "今年以来");
        result.add(fromYear);
        return Results.newListResultEntity(result);
    }

    /**
     * 127.0.0.1:8081/trade/get_gain_contrast
     * 获取首页各种指数收益的折线对比  网站首页接口需要更改
     * 近1月(31)  近3月(90)  近6月(180)  近一年(365)
     *
     * @return
     */
    @GetMapping(path = "/get_gain_contrast")
    public ResultEntity getContrast(@RequestParam(defaultValue = "365") Integer diff) {
        String userCode = getUserCode();

        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        YieldRateVo result = gainRateService.getIndexTrend(userCode, diff, LocalDate.now());
        return Results.newSingleResultEntity(result);
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
        TotalAccountBo totalAccountBo = TotalAccountBo.builder().date(userInfo.getCreatedTime().toLocalDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                .initAmount(userInfo.getInitAmount())
                .relativeProfit(totalBeatByUserCode.getRelativeProfit())
                .relativeProfitRate(totalBeatByUserCode.getRelativeProfitRate())
                .beatStandardRate(totalBeatByUserCode.getBeatStandardRate())
                //平均仓位的计算方式
                .avgPosition((BigDecimal.ONE.subtract(assetService.getAvgIdleRate(userCode))).multiply(new BigDecimal(100)))
                .repoProfit(now.getTotalRepoProfit())
                .repoProfitRate(BigDecimal.ZERO)
                .build();
        if (now.getTotalRepoAmount().compareTo(BigDecimal.ZERO) > 0) {
            //
            BigDecimal profitRate = now.getTotalRepoProfit().divide(now.getTotalRepoAmount(), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
            totalAccountBo.setRepoProfitRate(profitRate);
        }
        AccountSummaryVo result = new AccountSummaryVo();
        result.setTodayAccountBo(todayAccountBo);
        result.setTotalAccountBo(totalAccountBo);
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
        Page<TradeRecordVo> result = tradeRecordService.search(userCode, baseSearchVo);
        return CollectionUtils.isEmpty(result.getList())
                ? Results.newPageResultEntity(0L, new ArrayList<>(0))
                : Results.newPageResultEntity(result.getTotal(), result.getList());
    }

    @GetMapping(path = "/search_reverse_repo")
    public ResultEntity searchRepo(BaseSearchVo baseSearchVo) {
        ValidationUtils.validate(baseSearchVo);
        String userCode = getUserCode();
        Page<ReverseRepo> search = reverseRepoService.search(userCode, baseSearchVo);

        if (search.getTotal() == 0) {
            return Results.newPageResultEntity(0L, new ArrayList<>(0));
        }
        List<ReverseRepoVO> data = ConvertUtils.listConvert(search.getList(), ReverseRepoVO.class);
        return Results.newPageResultEntity(search.getTotal(), data);
    }


    private String getUserCode() {
        String userCode = RequestContext.getInstance().getAmountShare();
        userCode = StringUtils.isEmpty(userCode) ? "10000000" : userCode;
        return userCode;
    }


}

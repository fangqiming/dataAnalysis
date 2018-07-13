package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.EndAssetBo;
import com.i000.stock.user.api.entity.bo.PageIndexValueBo;
import com.i000.stock.user.api.entity.bo.StartAssetBo;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
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

    /**
     * 127.0.0.1:8081/trade/find_gain
     * 获取首页的最近获利情况描述
     * 基本完成
     *
     * @return
     */
    @GetMapping(path = "/find_gain")
    public ResultEntity findProfit() {
        String userCode = RequestContext.getInstance().getAccountCode();
        Asset asset = assetService.getLately(userCode);
        String date = asset.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd"));
        List<PageGainVo> result = new ArrayList<>(4);

        result.add(gainRateService.getRecentlyGain(userCode, 1, asset.getDate(), date + " 当天"));
        result.add(gainRateService.getRecentlyGain(userCode, 31, asset.getDate(), "最近一月"));
        PageGainVo fromYear = gainRateService.getFromYearStart(userCode, 370, asset.getDate(), "今年以来");
        result.add(fromYear);
        result.add(gainRateService.getYearRate(fromYear, asset.getDate()));
        return Results.newListResultEntity(result);
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
            baseLineTrendVO.getAiMarket().add(baseLine.getAiMarket().divide(new BigDecimal(10), 0, BigDecimal.ROUND_UP));
            baseLineTrendVO.getBaseMarket().add(baseLine.getBaseMarket().divide(new BigDecimal(10), 0, BigDecimal.ROUND_UP));
            baseLineTrendVO.getTime().add(baseLine.getTime());
        });
        return Results.newSingleResultEntity(baseLineTrendVO);
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
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        YieldRateVo result = gainRateService.getIndexTrend(userCode, diff, LocalDate.now());
        return Results.newSingleResultEntity(result);
    }


    /**
     * 127.0.0.1:8081//trade/get_asset_summary
     * 获取首页的账户总览信息
     * 经过测试基本可用
     */
    @GetMapping(path = "/get_asset_summary")
    public ResultEntity getAssetSummary() {
        String userCode = RequestContext.getInstance().getAccountCode();
        Asset asset = assetService.getLately(userCode);
        UserInfo userInfo = userInfoService.getByName(userCode);
        if (Objects.isNull(asset)) {
            //此处的账户总览需要获取用户是什么时候创建的账户
            StartAssetBo startAssetBo = StartAssetBo.builder().date(userInfo.getCreatedTime().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                    .totalAsset(userInfo.getInitAmount())
                    .balanceAmount(BigDecimal.ZERO)
                    .stockAmount(BigDecimal.ZERO)
                    .todayProfit(BigDecimal.ZERO)
                    .avgPosition((BigDecimal.ZERO)).build();
            EndAssetBo endAssetBo = EndAssetBo.builder().date(userInfo.getCreatedTime().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                    .totalAsset(userInfo.getInitAmount())
                    .balanceAmount(BigDecimal.ZERO)
                    .stockAmount(BigDecimal.ZERO)
                    .totalProfit(BigDecimal.ZERO)
                    .todayPosition(BigDecimal.ZERO).build();
            AssetSummaryVo result = AssetSummaryVo.builder().start(startAssetBo).end(endAssetBo).build();
            return Results.newSingleResultEntity(result);
        }

        //todo 此处需要修改
        StartAssetBo startAssetBo = StartAssetBo.builder().date(userInfo.getCreatedTime().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                .totalAsset(userInfo.getInitAmount())
                .balanceAmount(BigDecimal.ZERO)
                .stockAmount(BigDecimal.ZERO)
                .todayProfit(asset.getGain())
                .avgPosition((BigDecimal.ONE.subtract(assetService.getAvgIdleRate(userCode))).multiply(new BigDecimal(100))).build();
        EndAssetBo endAssetBo = EndAssetBo.builder().date(asset.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")))
                .totalAsset(asset.getBalance().add(asset.getStock()))
                .balanceAmount(asset.getBalance())
                .stockAmount(asset.getStock())
                .totalProfit(asset.getTotalGain())
                .todayPosition((BigDecimal.ONE.subtract(assetService.getIdleRate(userCode))).multiply(new BigDecimal(100))).build();
        AssetSummaryVo result = AssetSummaryVo.builder().start(startAssetBo).end(endAssetBo).build();
        return Results.newSingleResultEntity(result);
    }


    /**
     * 127.0.0.1:8081/trade/find_stock
     * 首页获取当前的持仓信息  基本通过测试
     *
     * @return
     */
    @GetMapping(path = "/find_stock")
    public ResultEntity findHoldStock() {
        String userCode = RequestContext.getInstance().getAccountCode();
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
            }
            return Results.newListResultEntity(holdNowVos);
        }
        return Results.newListResultEntity(new ArrayList<>(0));
    }

    /**
     * 获取操作统计的接口
     * 基本通过测试
     *
     * @return
     */
    @GetMapping(path = "/get_operator_summary")
    public ResultEntity getOperator() {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        OperatorVo operatorSummary = operateSummaryService.getOperatorSummary(userCode);
        return Results.newSingleResultEntity(operatorSummary);
    }


    /**
     * 分页获取交易详情的接口
     * 需要获取每天的交易记录
     */
    @GetMapping(path = "/search_trade")
    public ResultEntity searchTrade(BaseSearchVo baseSearchVo) {
        ValidationUtils.validate(baseSearchVo);
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Page<TradeRecordVo> result = tradeRecordService.search(userCode, baseSearchVo);
        return CollectionUtils.isEmpty(result.getList())
                ? Results.newPageResultEntity(0L, new ArrayList<>(0))
                : Results.newPageResultEntity(result.getTotal(), result.getList());
    }


}

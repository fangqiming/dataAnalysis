package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.EndAssetBo;
import com.i000.stock.user.api.entity.bo.IpInfoBo;
import com.i000.stock.user.api.entity.bo.PageIndexValueBo;
import com.i000.stock.user.api.entity.bo.StartAssetBo;
import com.i000.stock.user.api.entity.vo.*;
import com.i000.stock.user.api.service.*;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.CodeEnumUtil;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.LineGroupQuery;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.bo.StepEnum;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private AccessService accessService;

    @Resource
    private ExternalServiceImpl externalService;

    @Resource
    private IndexGainService indexGainService;


    @Resource
    private LineService lineService;

    @Resource
    private UserInfoService userInfoService;

    @Autowired
    private CompanyService companyService;

    /**
     * 获取预期年化收益
     * 通过测试
     * @return
     */
    @GetMapping(path = "/get_year_rate")
    public ResultEntity getYearRate() {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Asset asset = assetService.getLately(userCode);
        int days = asset.getDate().getDayOfYear();
        BigDecimal yearRate = asset.getTotalGain().multiply(new BigDecimal(36500)).divide(new BigDecimal(days), 2, BigDecimal.ROUND_UP);
        return Results.newNormalResultEntity("yearRate", yearRate);
    }

    /**
     * 127.0.0.1:8081/trade/find_gain
     * 获取首页的最近获利情况描述
     * 基本完成
     *
     * @param httpServletRequest
     * @return
     */
    @GetMapping(path = "/find_gain")
    public ResultEntity findProfit(HttpServletRequest httpServletRequest) {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Asset lately = assetService.getLately(userCode);
        List<PageGainVo> result = new ArrayList<>(4);
        if (Objects.nonNull(lately) && Objects.nonNull(lately.getDate())) {
            LocalDate current = lately.getDate();
            result.add(getGain(current, 1, current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), userCode));
            result.add(getGain(lately.getDate(), 31, "最近一月", userCode));
            //todo 实际是最近1年，需要修改代码
            result.add(getGain(lately.getDate(), 365, "今年以来", userCode));
            //todo 实际是近3年来 需要修改代码
            result.add(getGain(lately.getDate(), 365 * 3, "成立以来", userCode));
        }
        saveAccess(httpServletRequest);
        return Results.newListResultEntity(result);
    }


    /**
     * 127.0.0.1:8082/recommend/get_index_contrast
     * 获取首页上证指数与千古指数的对比折线图
     *
     * @param step
     * @return
     */
    @GetMapping(path = "/get_index_contrast")
    public ResultEntity getBaseLineTrend(@RequestParam String step) {
        StepEnum stepEnum = CodeEnumUtil.transformationStr2Enum(step, StepEnum.class);
        List<LineGroupQuery> baseLines = stepIsDay(stepEnum)
                ? lineService.findBaseLineDay(stepEnum)
                : lineService.findBaseLineGroup(stepEnum);

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
     * 获取首页各种指数收益的折线对比
     * todo 应该已经完成，导入指数数据之后进行测试
     *
     * @return
     */
    @GetMapping(path = "/get_gain_contrast")
    public ResultEntity getContrast() {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Asset lately = assetService.getLately(userCode);
        YieldRateVo result = new YieldRateVo();
        if (Objects.nonNull(lately)) {
            //查询了最近一年的账户信息  todo 代码需要修改 365和下面的find 需要统一。
            List<Asset> diff = assetService.findDiff(lately.getDate(), 365, userCode);
            //获取到指数信息
            Map<LocalDate, List<IndexGain>> indexInfo = indexGainService.find().stream().collect(groupingBy(IndexGain::getDate));

            List<Asset> collect = diff.stream().sorted(Comparator.comparing(Asset::getDate)).collect(toList());

            for (Asset asset : collect) {
                result.getStockGain().add(asset.getTotalGain().multiply(new BigDecimal(100)));
                result.getTime().add(asset.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
                result.getCybGain().add(indexInfo.get(asset.getDate()).get(0).getCybTotal().multiply(new BigDecimal(100)));
                result.getSzGain().add(indexInfo.get(asset.getDate()).get(0).getSzTotal().multiply(new BigDecimal(100)));
                result.getHsGain().add(indexInfo.get(asset.getDate()).get(0).getHsTotal().multiply(new BigDecimal(100)));
            }
        }
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
        StartAssetBo startAssetBo = StartAssetBo.builder().date("18-01-01")
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
     * //现在查一个股票名称的字段
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
















    private void saveAccess(HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        IpInfoBo ipInfo = externalService.getIpInfo(ip);
        Access access = Access.builder().address(ip).city(ipInfo.getCity())
                .country(ipInfo.getCountry()).date(LocalDateTime.now()).build();
        if (Objects.nonNull(access.getCountry())) {
            accessService.save(access);
        }
    }

    private PageGainVo getGain(LocalDate date, Integer diff, String title, String userCode) {
        PageIndexValueBo indexGain = indexGainService.getDiffGain(date, diff);
        GainBo gain = assetService.getGain(date, diff, userCode);
        List<GainVo> gainVos = new ArrayList<>(4);
        gainVos.add(GainVo.builder().indexName("千古指数").profit(gain.getProfit()).build());
        gainVos.add(GainVo.builder().indexName("上证指数").profit(indexGain.getSzGain()).build());
        gainVos.add(GainVo.builder().indexName("沪深300指").profit(indexGain.getHsGain()).build());
        gainVos.add(GainVo.builder().indexName("创业板指").profit(indexGain.getCybGain()).build());
        return PageGainVo.builder().Title(title).gain(gainVos).build();
    }

    private boolean stepIsDay(StepEnum step) {
        return step.equals(StepEnum.WEEK) || step.equals(StepEnum.MONTH);
    }

}

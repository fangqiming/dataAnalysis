package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.IpInfoBo;
import com.i000.stock.user.api.entity.vo.*;
import com.i000.stock.user.api.service.AccessService;
import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.HoldNowService;
import com.i000.stock.user.api.service.TradeRecordService;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.Access;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.HoldNow;
import com.i000.stock.user.dao.model.TradeRecord;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private TradeRecordService tradeRecordService;

    @Resource
    private AccessService accessService;

    @Resource
    private ExternalServiceImpl externalService;

    /**
     * 127.0.0.1:8082/trade/get_contrast
     * 获取用户从开始投入到目前的收益率之和的曲线
     *
     * @return
     */
    @GetMapping(path = "/get_contrast")
    public ResultEntity getContrast() {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Asset lately = assetService.getLately(userCode);
        List<Asset> diff = assetService.findDiff(lately.getDate(), 365, userCode);
        List<Asset> collect = diff.stream().sorted(Comparator.comparing(Asset::getDate)).collect(toList());
        List<BigDecimal> gain = new ArrayList<>();
        List<String> time = new ArrayList<>();
        for (Asset asset : collect) {
            gain.add(asset.getTotalGain().multiply(new BigDecimal(100)));
            time.add(asset.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
        }
        return Results.newSingleResultEntity(YieldRateVo.builder().gain(gain).time(time).build());
    }

    /**
     * 127.0.0.1:8082/trade/find_gain
     * 首先首页获取最新的收益情况，他需要知道目前最新的日期是什么
     * 首页获取最新获利情况的展示
     *
     * @return
     */
    @GetMapping(path = "/find_gain")
    public ResultEntity findProfit(HttpServletRequest httpServletRequest) {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Asset lately = assetService.getLately(userCode);
        List<GainVo> result = new ArrayList<>();
        if (Objects.nonNull(lately) && Objects.nonNull(lately.getDate())) {
            result.add(GainVo.builder().date(lately.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).profit(lately.getGain()).build());
            result.add(getGain(lately.getDate(), 31, "最近一月", userCode));
            result.add(getGain(lately.getDate(), 365, "最近一年", userCode));
            result.add(getGain(lately.getDate(), 365 * 3, "最近三年", userCode));
        }
        saveAccess(httpServletRequest);
        return Results.newListResultEntity(result);
    }


    /**
     * 分页查找收益信息
     * 127.0.0.1:8082/trade/search_gain
     *
     * @param baseSearchVo
     * @return
     */
    @GetMapping(path = "/search_gain")
    public ResultEntity searchGain(BaseSearchVo baseSearchVo) {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        ValidationUtils.validate(baseSearchVo);
        Page<Asset> search = assetService.search(baseSearchVo, userCode);
        return CollectionUtils.isEmpty(search.getList()) ? Results.newPageResultEntity(0L, new ArrayList<>(0)) :
                Results.newPageResultEntity(search.getTotal(), ConvertUtils.listConvert(search.getList(), AssetVo.class));
    }

    /**
     * 127.0.0.1:8082/trade/find_trade
     * 查找指定的日期的交易记录
     */
    @GetMapping(path = "/find_trade")
    public ResultEntity findTrade(@RequestParam("date") LocalDate date) {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        ValidationUtils.validateParameter(date, "日期不能为空");
        List<TradeRecord> tradeRecords = tradeRecordService.find(date, userCode);
        return Results.newListResultEntity(ConvertUtils.listConvert(tradeRecords, TradeRecordVo.class));
    }

    /**
     * 查找最新交易记录
     * 127.0.0.1:8082/trade/find_new_trade
     *
     * @return
     */
    @GetMapping(path = "/find_new_trade")
    public ResultEntity findNewTrade() {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        LocalDate date = tradeRecordService.getMaxDate(userCode);
        List<TradeRecord> tradeRecords = tradeRecordService.find(date, userCode);
        return Results.newListResultEntity(ConvertUtils.listConvert(tradeRecords, TradeRecordVo.class));
    }

    /**
     * 127.0.0.1:8082/trade/find_stock
     * 获取当前持股信息
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
            }
            return Results.newListResultEntity(holdNowVos);
        }
        return Results.newListResultEntity(new ArrayList<>(0));
    }

    /**
     * 127.0.0.1:8082/trade/get_overview
     * 获取账户总览信息
     *
     * @return
     */
    @GetMapping(path = "/get_overview")
    public ResultEntity getOverview() {
        String userCode = RequestContext.getInstance().getAccountCode();
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        return Results.newSingleResultEntity(assetService.getSummary(userCode));
    }

    private GainVo getGain(LocalDate date, int day, String DateStr, String userCode) {
        GainBo gain = assetService.getGain(date, day, userCode);
        return GainVo.builder().date(DateStr).profit(gain.getProfit()).build();
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

}

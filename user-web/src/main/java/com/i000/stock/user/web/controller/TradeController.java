package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.HoldNowService;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.api.entity.vo.*;
import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.TradeService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.HoldNow;
import com.i000.stock.user.dao.model.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private TradeService tradeService;

    @Resource
    private HoldNowService holdNowService;

    /**
     * 127.0.0.1:8082/trade/get_contrast
     * 获取用户从开始投入到目前的收益率之和的曲线
     *
     * @param userCode
     * @return
     */
    @GetMapping(path = "/get_contrast")
    public ResultEntity getContrast(String userCode) {
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Asset lately = assetService.getLately(userCode);
        List<Asset> diff = assetService.findDiff(lately.getDate(), 365, userCode);
        List<Asset> collect = diff.stream().sorted(Comparator.comparing(Asset::getDate)).collect(toList());
        List<BigDecimal> gain = new ArrayList<>();
        List<String> time = new ArrayList<>();
        for (Asset asset : collect) {
            gain.add(asset.getTotalGain());
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
    public ResultEntity findProfit(String userCode) {
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        Asset lately = assetService.getLately(userCode);
        List<GainVo> result = new ArrayList<>();
        if (Objects.nonNull(lately) && Objects.nonNull(lately.getDate())) {
            result.add(GainVo.builder().date(lately.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).profit(lately.getGain()).build());
            result.add(getGain(lately.getDate(), 31, "最近一月", userCode));
            result.add(getGain(lately.getDate(), 365, "最近一年", userCode));
            result.add(getGain(lately.getDate(), 365 * 3, "最近三年", userCode));
        }
        return Results.newListResultEntity(result);

    }

    private GainVo getGain(LocalDate date, int day, String DateStr, String userCode) {
        GainBo gain = assetService.getGain(date, day, userCode);
        return GainVo.builder().date(DateStr).profit(gain.getProfit()).build();
    }

    /**
     * 分页查找收益信息
     * 127.0.0.1:8082/trade/search_gain
     *
     * @param baseSearchVo
     * @return
     */
    @GetMapping(path = "/search_gain")
    public ResultEntity searchGain(BaseSearchVo baseSearchVo, String userCode) {
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        ValidationUtils.validate(baseSearchVo);
        Page<Asset> search = assetService.search(baseSearchVo, userCode);
        return CollectionUtils.isEmpty(search.getList()) ? Results.newPageResultEntity(0L, new ArrayList<>(0)) :
                Results.newPageResultEntity(search.getTotal(), ConvertUtils.listConvert(search.getList(), AssetVo.class));
    }

    /**
     * 127.0.0.1:8082/trade/get_lately
     * 获取最新的交易记录
     *
     * @return
     */
    @GetMapping(path = "/get_lately")
    public ResultEntity getLatelyTrade() {
        LocalDate date = tradeService.getMaxDate();
        List<Trade> byDate = tradeService.findByDate(date);
        return Results.newListResultEntity(ConvertUtils.listConvert(byDate, TradeVo.class));
    }

    /**
     * todo  需要去掉
     * 127.0.0.1:8082/trade/search
     * 分页查看交易记录，按照天分页
     */
    @GetMapping(path = "/search")
    public ResultEntity search(BaseSearchVo baseSearchVo, String userCode) {
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        baseSearchVo.setPageNo(Objects.isNull(baseSearchVo.getPageNo()) ? 1 : baseSearchVo.getPageNo());
        ValidationUtils.validate(baseSearchVo);
        Page<Asset> search = assetService.search(baseSearchVo, userCode);
        Page<Asset> pageData = search;
        if (CollectionUtils.isEmpty(pageData.getList())) {
            return Results.newPageResultEntity(0L, null);
        }
        //然后获取这些日期的交易记录
        List<LocalDate> dates = pageData.getList().stream().map(a -> a.getDate()).collect(Collectors.toList());
        List<Trade> userTrades = tradeService.findByDate(dates);
        //根据日期进行排序
        Map<LocalDate, List<Trade>> map = userTrades.stream().collect(groupingBy(Trade::getDate));
        //然后将交易记录以及获利情况返回给前台
        List<TradeGainVo> result = new ArrayList<>();
        for (Asset userProfit : pageData.getList()) {
            result.add(TradeGainVo.builder().date(userProfit.getDate())
                    .gainRate(userProfit.getGain())
                    .trade(ConvertUtils.listConvert(map.get(userProfit.getDate()), TradeVo.class)).build());
        }
        return Results.newPageResultEntity(pageData.getTotal(), result);
    }

    /**
     * 127.0.0.1:8082/trade/find_stock
     * 获取当前持股信息
     *
     * @return
     */
    @GetMapping(path = "/find_stock")
    public ResultEntity findHoldStock(@RequestParam("userCode") String userCode) {
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
    public ResultEntity getOverview(String userCode) {
        ValidationUtils.validateParameter(userCode, "用户码不能为空");
        return Results.newSingleResultEntity(assetService.getSummary(userCode));
    }


}

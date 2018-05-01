package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.HoldService;
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
import com.i000.stock.user.dao.model.Hold;
import com.i000.stock.user.dao.model.Trade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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
    private HoldService holdService;

    /**
     * 127.0.0.1:8082/trade/find_gain
     * 首先首页获取最新的收益情况，他需要知道目前最新的日期是什么
     * 首页获取最新获利情况的展示
     *
     * @return
     */
    @GetMapping(path = "/find_gain")
    public ResultEntity findProfit() {
        Asset lately = assetService.getLately();
        List<GainVo> result = new ArrayList<>();
        if (Objects.nonNull(lately) && Objects.nonNull(lately.getDate())) {
            result.add(GainVo.builder().date(lately.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).profit(lately.getGain()).build());
            Asset diff = assetService.getDiff(lately.getDate(), 1);
            result.add(GainVo.builder().date(diff.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).profit(diff.getGain()).build());
            result.add(getGain(lately.getDate(), 7, "最近一周"));
            result.add(getGain(lately.getDate(), 31, "最近一月"));
        }
        return Results.newListResultEntity(result);

    }

    private GainVo getGain(LocalDate date, int day, String DateStr) {
        GainBo gain = assetService.getGain(date, day);
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
    public ResultEntity searchGain(BaseSearchVo baseSearchVo) {
        ValidationUtils.validate(baseSearchVo);
        Page<Asset> search = assetService.search(baseSearchVo);
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
     * 127.0.0.1:8082/trade/search
     * 分页查看交易记录，按照天分页
     */
    @GetMapping(path = "/search")
    public ResultEntity search(BaseSearchVo baseSearchVo) {
        baseSearchVo.setPageNo(Objects.isNull(baseSearchVo.getPageNo()) ? 1 : baseSearchVo.getPageNo());
        ValidationUtils.validate(baseSearchVo);
        Page<Asset> search = assetService.search(baseSearchVo);
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
    public ResultEntity findHoldStock() {
        List<Hold> hold = holdService.findHold();
        return Results.newListResultEntity(ConvertUtils.listConvert(hold, HoldVo.class));
    }

    /**
     * 127.0.0.1:8082/trade/get_overview
     * 获取账户总览信息
     *
     * @return
     */
    @GetMapping(path = "/get_overview")
    public ResultEntity getOverview() {
        return Results.newSingleResultEntity(assetService.getSummary());
    }

}

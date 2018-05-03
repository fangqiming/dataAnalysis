package com.i000.stock.user.service.impl;


import com.i000.stock.user.api.entity.vo.AssetDiffVo;
import com.i000.stock.user.api.entity.vo.GainBo;
import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.HoldNowService;
import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.api.service.UserInfoService;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.mapper.AssetMapper;
import com.i000.stock.user.dao.mapper.HoldMapper;
import com.i000.stock.user.dao.mapper.TradeMapper;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.asset.amount.UpdateAssetImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:34 2018/4/26
 * @Modified By:
 */
@Component
@Transactional
public class AssetServiceImpl implements AssetService {


    @Autowired
    private UpdateAssetImpl updateAsset;

    @Resource
    private AssetMapper assetMapper;

    @Resource
    private HoldNowService holdNowService;


    @Override
    public Asset getLately(String userCode) {
        return assetMapper.getLately(userCode);
    }

    @Override
    public Asset getDiff(LocalDate date, Integer day, String userCode) {
        return assetMapper.getDiff(date, day, userCode);
    }

    @Override
    public List<Asset> findDiff(LocalDate date, Integer day, String userCode) {
        return assetMapper.findDiff(date, day, userCode);
    }

    /**
     * 根据推荐数据计算出资产信息保存到数据库中
     *
     * @param date
     */
    @Override
    public void calculate(LocalDate date, String userCode, List<Hold> trade) {
        Asset now = assetMapper.getLately(userCode);
        if (Objects.nonNull(now) && Objects.nonNull(date)
                && !CollectionUtils.isEmpty(trade) && date.compareTo(now.getDate()) <= 0) {
            return;
        }
        now.setDate(date);
        updateAsset(trade, now);
        holdNowService.updatePrice(date);
        //设置股票金额
        now.setStock(getStockAmount(userCode));
        //设置相对上一次的收益率
        Asset lately = getLately(userCode);
        now.setGain(getGain(now, lately));
        //设置相对最开的的总的收益率
        Asset diff = assetMapper.getDiff(date, 36500, userCode);
        now.setTotalGain(getGain(now, diff));
        //保存到数据
        assetMapper.insert(now);
    }

    private Asset updateAsset(List<Hold> trade, Asset now) {
        if (!CollectionUtils.isEmpty(trade)) {
            //先处理卖的，这样余额就能增加，以便能够买别的股票
            List<Hold> sell = trade.stream().filter(a -> a.getAction().equals("SELL")).collect(toList());
            for (Hold hold : sell) {
                now = updateAsset.getParse(hold.getAction()).update(now, hold);
            }
            List<Hold> notSell = trade.stream().filter(a -> !a.getAction().equals("SELL")).collect(toList());
            for (Hold hold : notSell) {
                now = updateAsset.getParse(hold.getAction()).update(now, hold);
            }
        }
        return now;
    }

    private BigDecimal getStockAmount(String userCode) {
        List<HoldNow> holdNows = holdNowService.find(userCode);
        List<BigDecimal> collect = holdNows.stream().map(a -> a.getNewPrice().multiply(new BigDecimal(a.getAmount()))).collect(toList());
        return collect.stream().collect(reducing(new BigDecimal(0), (a, b) -> a.add(b)));
    }

    private BigDecimal getGain(Asset now, Asset befor) {
        BigDecimal nowAmount = now.getBalance().add(now.getStock()).add(now.getCover());
        BigDecimal beforAmount = befor.getBalance().add(befor.getStock()).add(befor.getCover());
        return (nowAmount.subtract(beforAmount)).divide(beforAmount, 4, RoundingMode.HALF_UP);
    }

    @Override
    public GainBo getGain(LocalDate start, Integer day, String userCode) {
        GainBo result = GainBo.builder().startDate(start).profit(new BigDecimal(0)).build();
        Asset byDate = assetMapper.getByDate(start, userCode);
        if (Objects.isNull(byDate)) {
            return result;
        }
        Asset diff = getDiff(start, day, userCode);
        if (Objects.isNull(diff)) {
            return result;
        }
        result.setEndDate(diff.getDate());
        result.setProfit(getGain(byDate, diff));
        return result;
    }

    @Override
    public Page<Asset> search(BaseSearchVo baseSearchVo, String userCode) {
        baseSearchVo.setStart();
        List<Asset> search = assetMapper.search(baseSearchVo, userCode);
        Page<Asset> result = new Page<>();
        result.setList(search);
        result.setTotal(assetMapper.pageTotal());
        return result;
    }

    @Override
    public AssetDiffVo getSummary(String userCode) {

        Asset now = assetMapper.getLately(userCode);
        Asset old = assetMapper.getDiff(now.getDate(), 36500, userCode);
        return AssetDiffVo.builder().startDate(old.getDate())
                .date(now.getDate())
                .initAmount(old.getBalance())
                .totalAmount(now.getBalance().add(now.getCover()).add(now.getStock()))
                .stockAmount(now.getStock())
                .balance(now.getBalance())
                .todayGain(now.getGain())
                .totalGain(now.getTotalGain())
                .coverAmount(now.getCover()).build();

    }
}

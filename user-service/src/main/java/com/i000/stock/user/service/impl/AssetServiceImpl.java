package com.i000.stock.user.service.impl;


import com.i000.stock.user.api.entity.vo.GainBo;
import com.i000.stock.user.api.service.buiness.AssetService;
import com.i000.stock.user.api.service.buiness.HoldNowService;
import com.i000.stock.user.api.service.buiness.UserInfoService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.mapper.AssetMapper;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.operate.UpdateAssetImpl;
import com.sun.tools.javac.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private UserInfoService userInfoService;


    @Override
    public Asset getLately(String userCode) {
        return assetMapper.getLately(userCode);
    }

    @Override
    public Asset getDiff(LocalDate date, Integer day, String userCode) {
        return assetMapper.getDiff(date, day, userCode);
    }

    @Override
    public Asset getDiff(LocalDate date, String userCode) {
        return assetMapper.getDiff_2(userCode, date);
    }

    @Override
    public Asset getDiffByGt(LocalDate date, String userCode) {
        return assetMapper.getDiffByGt(userCode,date);
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
    public void calculate(LocalDate date, String userCode, List<Hold> trade, List<Hold> initTrade) {
        //获取最初的资产信息
        Asset init = assetMapper.getLately(userCode);
        //推荐的日期小于现在的资产日期，直接退出
        if (!Objects.isNull(init) && init.getDate().compareTo(date) >= 0) {
            return;
        }
        //是新用户的标记
        Boolean isNewUser = false;
        if (Objects.isNull(init)) {
            isNewUser = true;
            //获取到用户的初始资产信息
            init = getAssetByUserCode(userCode);
        }
        //深拷贝
        Asset now = ConvertUtils.beanConvert(init, new Asset());
        now.setDate(date);
        if (Objects.nonNull(date) && !CollectionUtils.isEmpty(trade) && date.compareTo(now.getDate()) < 0) {
            return;
        }

        // todo 根据当天的交易记录，更新用户的资产信息,,此处如果判断交易记录为空保存空即可满足要求
        updateAsset(isNewUser ? initTrade : trade, now);

        //根据推送过来的股票价格，更新持股的价格数据
        holdNowService.updatePrice(date);

        //设置股票金额
        now.setStock(getStockAmount(userCode));
        //设置相对上一次的收益率
        Asset lately = getLately(userCode);
        lately = Objects.isNull(lately) ? init : lately;
        now.setGain(getGain(now, lately));
        //设置相对最开的的总的收益率
        Asset diff = assetMapper.getDiff(date, 36500, userCode);
        diff = Objects.isNull(diff) ? init : diff;
        now.setTotalGain(getGain(now, diff));
        //保存到数据
        assetMapper.insert(now);
    }

    private Asset getAssetByUserCode(String userCode) {
        UserInfo byName = userInfoService.getByName(userCode);
        return Asset.builder()
                .balance(byName.getInitAmount())
                .cover(new BigDecimal(0))
                .gain(new BigDecimal(0))
                .stock(new BigDecimal(0))
                .totalGain(new BigDecimal(0))
                .userCode(userCode).build();
    }

    private Asset updateAsset(List<Hold> trade, Asset now) {
        if (!CollectionUtils.isEmpty(trade)) {
            //先处理卖的，这样余额就能增加，以便能够买别的股票  如果有拆股的就需要更新股票的价格......
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
        return (nowAmount.subtract(beforAmount)).divide(beforAmount, 4, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public GainBo getGain(LocalDate start, Integer day, String userCode) {
        GainBo result = GainBo.builder().startDate(start).profit(new BigDecimal(0)).build();
        //查询到了当前的账户情况
        Asset byDate = assetMapper.getByDate(start, userCode);
        if (Objects.isNull(byDate)) {
            return result;
        }
        //查询到了之前的账户情况
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
    public BigDecimal getAvgIdleRate(String userCode) {
        return assetMapper.getAvgIdleRate(userCode);
    }

    @Override
    public BigDecimal getIdleRate(String userCode) {
        return assetMapper.getIdleRate(userCode);
    }

    @Override
    public List<Asset> findAssetBetween(String userCode, LocalDate start, LocalDate end) {
        return assetMapper.findBetween(userCode, start, end);
    }

    @Override
    public Asset getYearFirst(String year, String userCode) {
        return assetMapper.getYearFirst(year, userCode);
    }

    @Override
    public BigDecimal getMaxGain(String userCode) {
        return assetMapper.getMaxGain(userCode);
    }

    @Override
    public BigDecimal getMinGain(String userCode) {
        return assetMapper.getMinGain(userCode);
    }
}

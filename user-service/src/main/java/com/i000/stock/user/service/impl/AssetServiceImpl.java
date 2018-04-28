package com.i000.stock.user.service.impl;


import com.i000.stock.user.api.entity.vo.AssetDiffVo;
import com.i000.stock.user.api.entity.vo.GainBo;
import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.mapper.AssetMapper;
import com.i000.stock.user.dao.mapper.HoldMapper;
import com.i000.stock.user.dao.mapper.TradeMapper;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Hold;
import com.i000.stock.user.dao.model.Trade;
import com.i000.stock.user.service.impl.asset.UpdateAssetImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.i000.stock.user.dao.bo.Page;

import static java.util.stream.Collectors.reducing;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:34 2018/4/26
 * @Modified By:
 */
@Component
public class AssetServiceImpl implements AssetService {

    @Value("${business.init.amount}")
    private BigDecimal initAmount;

    @Autowired
    private UpdateAssetImpl updateAsset;

    @Resource
    private AssetMapper assetMapper;

    @Resource
    private TradeMapper tradeMapper;

    @Resource
    private HoldMapper holdMapper;

    @Override
    public Asset getLately() {
        return assetMapper.getLately();
    }

    @Override
    public Asset getDiff(LocalDate date, Integer day) {
        return assetMapper.getDiff(date, day);
    }

    /**
     * 根据推荐数据计算出资产信息保存到数据库中
     *
     * @param date
     */
    @Override
    public void calculate(LocalDate date) {
        Asset lately = assetMapper.getLately();
        if (Objects.nonNull(lately) && date.compareTo(lately.getDate()) <= 0) {
            return;
        }
        List<Trade> byDate = tradeMapper.findByDate(date);
        //更新账户信息
        for (Trade trade : byDate) {
            lately = updateAsset.getParse(trade.getAction()).update(lately, trade);
        }
        //计算总的股票资产
        List<Hold> byDate1 = holdMapper.findByDate(date);
        BigDecimal total = CollectionUtils.isEmpty(byDate1)
                ? new BigDecimal(0)
                : byDate1.stream().collect(reducing(new BigDecimal(0), Hold::getNewPrice, (a, b) -> a.add(b)));
        lately.setStock(total);

        Asset lately1 = getLately();
        if (Objects.isNull(lately1)) {
            lately.setGain(new BigDecimal(0));
        } else {
            BigDecimal now = lately.getStock().add(lately.getBalance()).subtract(lately.getCover());
            BigDecimal old = lately1.getStock().add(lately1.getBalance()).subtract(lately1.getCover());
            lately.setGain(now.subtract(old).divide(old, 5, RoundingMode.HALF_UP));
        }
        assetMapper.insert(lately);
    }

    @Override
    public GainBo getGain(LocalDate start, Integer day) {
        GainBo result = GainBo.builder().startDate(start).profit(new BigDecimal(0)).build();
        Asset byDate = assetMapper.getByDate(start);
        if (Objects.isNull(byDate)) {
            return result;
        }
        Asset diff = getDiff(start, day);
        if (Objects.isNull(diff)) {
            return result;
        }
        result.setEndDate(diff.getDate());
        BigDecimal now = byDate.getStock().add(byDate.getBalance()).subtract(byDate.getCover());
        BigDecimal old = diff.getStock().add(diff.getBalance()).subtract(diff.getCover());
        if (old.intValue() == 0) {
            return result;
        }
        result.setProfit(now.subtract(old).divide(old, 4, RoundingMode.HALF_UP));
        return result;
    }

    @Override
    public Page<Asset> search(BaseSearchVo baseSearchVo) {
        baseSearchVo.setStart();
        List<Asset> search = assetMapper.search(baseSearchVo);
        Page<Asset> result = new Page<>();
        result.setList(search);
        result.setTotal(assetMapper.pageTotal());
        return result;
    }

    @Override
    public AssetDiffVo getSummary() {
        AssetDiffVo assetDiffVo = new AssetDiffVo();
        assetDiffVo.setInitAmount(initAmount);
        Asset end = getLately();
        if (Objects.nonNull(end)) {
            GainBo gain = getGain(end.getDate(), 365000);
            assetDiffVo.setTotalAmount(initAmount.multiply((new BigDecimal(1).add(gain.getProfit()))));
            assetDiffVo.setDate(end.getDate());
            assetDiffVo.setTodayGain(end.getGain());
            assetDiffVo.setTotalGain(gain.getProfit());
            BigDecimal total = end.getBalance().add(end.getCover()).add(end.getStock());
            assetDiffVo.setStockAmount(end.getStock().divide(total, 5, RoundingMode.HALF_UP).multiply(total));
            assetDiffVo.setBalance(end.getBalance().divide(total, 5, RoundingMode.HALF_UP).multiply(total));
            assetDiffVo.setCoverAmount(end.getCover().divide(total, 5, RoundingMode.HALF_UP).multiply(total));
        }
        return assetDiffVo;
    }
}

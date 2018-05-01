package com.i000.stock.user.service.impl.asset.amount;

import com.i000.stock.user.api.service.AssetUpdateService;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Trade;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description: cover就是买入做空的股票
 * @Date:Created in 16:59 2018/4/26
 * @Modified By:
 */
public class CoverAssetImpl implements AssetUpdateService {
    @Override
    public Asset update(Asset asset, Trade trade) {
        if (Objects.nonNull(asset) && Objects.nonNull(asset.getCover())) {
            asset.setCover(asset.getCover().subtract(trade.getPrice()));
        } else {
            asset = Objects.nonNull(asset) ? asset : new Asset();
            asset.setCover(trade.getPrice().multiply(new BigDecimal(-1)));
        }
        asset.setDate(trade.getDate());
        return asset;
    }
}

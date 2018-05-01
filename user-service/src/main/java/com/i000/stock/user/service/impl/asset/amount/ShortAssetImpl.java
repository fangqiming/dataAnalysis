package com.i000.stock.user.service.impl.asset.amount;

import com.i000.stock.user.api.service.AssetUpdateService;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Trade;

import java.util.Objects;

/**
 * @Author:qmfang
 * @Description: 做空股票  账户资产增加  做空资产增加，到时需要补不回
 * @Date:Created in 16:59 2018/4/26
 * @Modified By:
 */
public class ShortAssetImpl implements AssetUpdateService {
    @Override
    public Asset update(Asset asset, Trade trade) {
        if (Objects.nonNull(asset) && Objects.nonNull(asset.getCover())) {
            asset.setCover(asset.getCover().add(trade.getPrice()));
        } else {
            asset = Objects.nonNull(asset) ? asset : new Asset();
            asset.setCover(trade.getPrice());
        }
        asset.setDate(trade.getDate());
        return asset;
    }
}

package com.i000.stock.user.service.impl.asset;

import com.i000.stock.user.api.service.AssetUpdateService;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Trade;

import java.util.Objects;

/**
 * @Author:qmfang
 * @Description: 卖股票 账户余额增加
 * @Date:Created in 16:58 2018/4/26
 * @Modified By:
 */
public class SellAssetImpl implements AssetUpdateService {
    @Override
    public Asset update(Asset asset, Trade trade) {
        if (Objects.nonNull(asset) && Objects.nonNull(asset.getBalance())) {
            asset.setBalance(asset.getBalance().add(trade.getPrice()));
        } else {
            asset = Objects.nonNull(asset) ? asset : new Asset();
            asset.setBalance(trade.getPrice());
        }
        asset.setDate(trade.getDate());
        return asset;
    }
}

package com.i000.stock.user.service.impl.asset.money;

import com.i000.stock.user.api.entity.bo.AssetInitBo;
import com.i000.stock.user.api.service.AssetUpdateService;
import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

@Component
public class SellAssetSomeImpl implements AssetUpdateService {

    @Autowired
    private AssetInitBo assetInitBo;

    @Resource
    private HoldService holdService;

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

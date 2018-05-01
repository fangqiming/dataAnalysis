package com.i000.stock.user.service.impl.asset.amount;

import com.i000.stock.user.api.service.AssetUpdateService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.service.impl.asset.amount.BuyAssetImpl;
import com.i000.stock.user.service.impl.asset.amount.CoverAssetImpl;
import com.i000.stock.user.service.impl.asset.amount.SellAssetImpl;
import com.i000.stock.user.service.impl.asset.amount.ShortAssetImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:27 2018/4/26
 * @Modified By:
 */
@Component
public class UpdateAssetImpl {

    private Map<String, AssetUpdateService> map;

    @PostConstruct
    private void init() {
        map = new HashMap<>(4);
        map.put("SELL", new SellAssetImpl());
        map.put("BUY", new BuyAssetImpl());
        map.put("SHORT", new ShortAssetImpl());
        map.put("COVER", new CoverAssetImpl());
    }

    public AssetUpdateService getParse(String action) {
        AssetUpdateService assetUpdateService = map.get(action);
        if (Objects.isNull(assetUpdateService)) {
            throw new ServiceException(ApplicationErrorMessage.INVALID_PARAMETER);
        }
        return assetUpdateService;
    }


}

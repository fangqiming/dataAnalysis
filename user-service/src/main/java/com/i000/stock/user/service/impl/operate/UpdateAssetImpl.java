package com.i000.stock.user.service.impl.operate;

import com.i000.stock.user.api.service.buiness.AssetUpdateService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SellAssetImpl sellAsset;
    @Autowired
    private BuyAssetImpl buyAsset;
    @Autowired
    private ShortAssetImpl shortAsset;
    @Autowired
    private CoverAssetImpl coverAsset;

    private Map<String, AssetUpdateService> map;

    @PostConstruct
    private void init() {
        map = new HashMap<>(4);
        map.put("SELL", sellAsset);
        map.put("BUY", buyAsset);
        map.put("SHORT", shortAsset);
        map.put("COVER", coverAsset);
    }

    public AssetUpdateService getParse(String action) {
        AssetUpdateService assetUpdateService = map.get(action);
        if (Objects.isNull(assetUpdateService)) {
            throw new ServiceException(ApplicationErrorMessage.INVALID_PARAMETER);
        }
        return assetUpdateService;
    }


}

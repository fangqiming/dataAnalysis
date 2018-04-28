package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Trade;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:56 2018/4/26
 * @Modified By:
 */
public interface AssetUpdateService {

    /**
     * 根据交易信息更细用户资产
     *
     * @param asset
     * @param plan
     * @return
     */
    Asset update(Asset asset, Trade plan);
}

package com.i000.stock.user.api.service.util;

import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.entity.bo.Price;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:59 2018/7/11
 * @Modified By:
 */
public interface IndexPriceCacheService {

    /**
     * 将指数信息放入缓存
     */
    List<IndexInfo> putIndexToCache(Integer tryNumber);

    /**
     * 将价格放入缓存
     */
    List<Price> putPriceToCache(Integer tryNumber);

    /**
     * 获取指数信息
     *
     * @return
     */
    List<IndexInfo> getIndex(Integer tryNumber);

    /**
     * 获取价格信息
     *
     * @return
     */
    List<Price> getPrice(Integer tryNumber);

    /**
     * 保存索引信息
     */
    void saveIndexValue();
}

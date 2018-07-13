package com.i000.stock.user.api.service.external;

import com.i000.stock.user.api.entity.bo.KVBo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:54 2018/7/4
 * @Modified By:
 */
public interface CompanyInfoCrawlerService {

    Map<String, List<KVBo>> CACHE = new ConcurrentHashMap<>();

    List<KVBo> putCache(String code);

    List<KVBo> getInfo(String code);

    default void clear() {
        CACHE.clear();
    }
}

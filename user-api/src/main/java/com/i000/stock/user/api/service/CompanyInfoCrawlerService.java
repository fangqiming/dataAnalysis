package com.i000.stock.user.api.service;

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

    //根据股票代码 获取公司的基本信息 以及 基本的成交量信息
    //http://stockpage.10jqka.com.cn/600309/
    List<KVBo> putCache(String code);

    List<KVBo> getInfo(String code);

    default void clear() {
        CACHE.clear();
    }
}

package com.i000.stock.user.api.service.util;

import com.i000.stock.user.dao.model.Ad;

/**
 * @Author:qmfang
 * @Description: 用于保存web页面的文案信息
 * @Date:Created in 16:15 2018/4/28
 * @Modified By:
 */
public interface AdService {

    /**
     * 通过key获取value
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 保存文案信息
     *
     * @param ad
     * @return
     */
    void save(Ad ad);
}

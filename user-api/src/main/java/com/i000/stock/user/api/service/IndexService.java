package com.i000.stock.user.api.service;

import com.i000.stock.user.api.entity.bo.IndexInfo;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:38 2018/4/25
 * @Modified By:
 */
public interface IndexService {

    /**
     * 获取实时的指数数据
     *
     * @return
     */
    List<IndexInfo> get();






}

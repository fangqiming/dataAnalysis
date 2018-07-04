package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.IndexValue;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:09 2018/7/4
 * @Modified By:
 */
public interface IndexValueService {

    /**
     * 查找到全部的IndexValue信息
     *
     * @return
     */
    List<IndexValue> findAll();
}

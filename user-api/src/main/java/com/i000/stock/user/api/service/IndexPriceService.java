package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.IndexPrice;

import java.io.IOException;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:02 2018/6/14
 * @Modified By:
 */
public interface IndexPriceService {

    /**
     * 根据日期获取信息
     *
     * @param date
     * @return
     */
    String getContent(String date);

    /**
     * 保存指数价格信息
     *
     * @param indexPrice
     */
    void save(IndexPrice indexPrice);

    /**
     * 获取当天的指数价格信息
     *
     * @return
     * @throws IOException
     */
    StringBuffer get() throws IOException;

}

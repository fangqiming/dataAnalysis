package com.i000.stock.user.api.service;

import com.i000.stock.user.api.entity.bo.Price;

import java.io.IOException;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:40 2018/4/25
 * @Modified By:
 */
public interface PriceService {

    /**
     * 获取全部的股票价格
     *
     * @return
     * @throws IOException
     */
    public StringBuffer get() throws IOException;
}

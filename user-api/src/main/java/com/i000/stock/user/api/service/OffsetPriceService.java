package com.i000.stock.user.api.service;

import java.io.IOException;

/**
 * @Author:qmfang
 * @Description: 用来计算股价偏移的服务
 * @Date:Created in 13:51 2018/6/21
 * @Modified By:
 */
public interface OffsetPriceService {

    void updateAmount() throws IOException;
}

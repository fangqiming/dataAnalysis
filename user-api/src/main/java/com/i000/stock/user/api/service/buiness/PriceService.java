package com.i000.stock.user.api.service.buiness;

import java.io.IOException;

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
    StringBuffer get() throws IOException;
}

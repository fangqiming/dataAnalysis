package com.i000.stock.user.api.service;

import java.io.IOException;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:03 2018/4/25
 * @Modified By:
 */
public interface CompanyService {

    /**
     * 从网页上爬取股票数据
     *
     * @return
     */
    List<String> getCode() throws IOException;
}

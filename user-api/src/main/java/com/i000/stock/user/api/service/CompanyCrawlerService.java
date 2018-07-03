package com.i000.stock.user.api.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:03 2018/4/25
 * @Modified By:
 */
public interface CompanyCrawlerService {

    /**
     * 从网页上爬取股票数据
     *
     * @return
     */
    List<String> getCode() throws IOException;


    /**
     * 从网页上爬取股票代码对应的公司名称
     *
     * @return
     * @throws IOException
     */
    Map<String, String> getCodeName() throws IOException;
}

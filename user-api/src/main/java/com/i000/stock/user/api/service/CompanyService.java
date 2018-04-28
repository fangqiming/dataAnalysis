package com.i000.stock.user.api.service;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:03 2018/4/25
 * @Modified By:
 */
public interface CompanyService {

    /**
     * 更新公司信息（与东方财富同步所有的A股股票）
     *
     * @return
     */
    Boolean updateInfo();
}

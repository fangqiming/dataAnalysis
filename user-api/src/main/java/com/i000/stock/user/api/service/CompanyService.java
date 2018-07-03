package com.i000.stock.user.api.service;

import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:46 2018/7/3
 * @Modified By:
 */
public interface CompanyService {

    /**
     * 批量保存公司信息
     *
     * @param info
     */
    void batchSave(Map<String, String> info);

    /**
     * 通过股票代码获取股票名称
     *
     * @param code
     * @return
     */
    String getNameByCode(String code);
}

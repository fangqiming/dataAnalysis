package com.i000.stock.user.api.service.bk;

import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:58 2018/7/25
 * @Modified By:
 */
public interface SingleFormulaService {

    /**
     * Map<day,sql>
     * @return
     */
    Map<String,String> findSql();
}

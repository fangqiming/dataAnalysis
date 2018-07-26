package com.i000.stock.user.api.service.bk;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:59 2018/7/26
 * @Modified By:
 */
public interface OtherFormulaService {

    /**
     * Map<code,value>  股票代码，对应的指标值
     *
     * @return
     */
    Map<String, BigDecimal> find(List<String> codes);
}

package com.i000.stock.user.api.service.bk;

import com.i000.stock.user.dao.model.Financial;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:02 2018/7/25
 * @Modified By:
 */
public interface FinancialService {

//    /**
//     * 单一指标直接计算出来
//     */
//    List<Financial> findBySingle(List<String> codes);
//
//    /**
//     * 符合指标一个一个的计算，然后聚合
//     *
//     * @return
//     */
//    Map<String, BigDecimal> findByComposite(List<String> codes);

    /**
     * 查找自选股的得分与排名
     *
     * @return
     */
    List<Financial> find();

}

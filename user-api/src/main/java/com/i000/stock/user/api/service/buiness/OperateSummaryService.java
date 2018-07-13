package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.api.entity.vo.OperatorVo;
import com.i000.stock.user.dao.model.OperateSummary;

/**
 * @Author:qmfang
 * @Description: 备注 id为1
 * @Date:Created in 18:22 2018/7/3
 * @Modified By:
 */
public interface OperateSummaryService {

    /**
     * 更新卖出次数 和 累计持有天数 , 亏本数  , 获利数
     */
    void updateSell(Integer holdDay, Integer profit, Integer loss,String userCode);

    /**
     * 更新买入次数
     */
    void updateBuy(String userCode);

    /**
     * 获取操作统计
     *
     * @return
     */
    OperatorVo getOperatorSummary(String userCode);
}

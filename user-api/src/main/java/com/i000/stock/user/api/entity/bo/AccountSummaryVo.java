package com.i000.stock.user.api.entity.bo;

import com.i000.stock.user.api.entity.vo.OperatorUsVO;
import com.i000.stock.user.api.entity.vo.OperatorVo;
import lombok.Data;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 19:09 2018/10/24
 * @Modified By:
 */
@Data
public class AccountSummaryVo {
    private TodayAccountBo todayAccountBo;
    private TotalAccountBo totalAccountBo;
    private OperatorVo operatorVo;
    private OperatorUsVO operatorUsVO;
}

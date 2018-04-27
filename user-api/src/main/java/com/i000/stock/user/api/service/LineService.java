package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.bo.StepEnum;
import com.i000.stock.user.dao.bo.LineGroupQuery;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:26 2018/4/27
 * @Modified By:
 */
public interface LineService {

    /**
     * 按照天数查询基准线的值
     * @param step
     * @return
     */
    List<LineGroupQuery> findBaseLineDay(StepEnum step);

    /**
     * 按照组查询基准线的天数
     * @param step
     * @return
     */
    List<LineGroupQuery> findBaseLineGroup(StepEnum step);
}

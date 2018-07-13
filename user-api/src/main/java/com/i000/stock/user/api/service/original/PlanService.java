package com.i000.stock.user.api.service.original;

import com.i000.stock.user.dao.model.Plan;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:49 2018/4/27
 * @Modified By:
 */
public interface PlanService {

    /**
     * 查询指定的交易日期的交易记录
     *
     * @param date
     * @return
     */
    List<Plan> findByDate(LocalDate date);

    /**
     * 查询最新的计划日期
     *
     * @return
     */
    LocalDate getMaxDate();

}

package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.original.PlanService;
import com.i000.stock.user.dao.mapper.PlanMapper;
import com.i000.stock.user.dao.model.Plan;
import com.i000.stock.user.dao.service.AbstractService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:50 2018/4/27
 * @Modified By:
 */
@Component
@Transactional
public class PlanServiceImpl extends AbstractService<Plan, PlanMapper> implements PlanService {
    @Override
    public List<Plan> findByDate(LocalDate date) {
        return baseMapper.findByDate(date);
    }

    @Override
    public LocalDate getMaxDate() {
        return baseMapper.getMaxDate();
    }
}

package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.service.PlanService;
import com.i000.stock.user.core.util.TimeUtil;
import com.i000.stock.user.dao.mapper.PlanMapper;
import com.i000.stock.user.dao.model.Plan;
import com.i000.stock.user.dao.service.AbstractService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:50 2018/4/27
 * @Modified By:
 */
@Component
public class PlanServiceImpl extends AbstractService<Plan, PlanMapper> implements PlanService {
    @Override
    public List<Plan> findByDate(LocalDate date) {
        return baseMapper.findByDate(date);
    }

    @Override
    public List<Plan> findByDate(List<LocalDate> dates) {
        List<String> date = TimeUtil.localData2StringList(dates);
        EntityWrapper<Plan> entityWrapper = createEntityWrapper();
        entityWrapper.in("new_date", date);
        return baseMapper.selectList(entityWrapper);
    }

    @Override
    public LocalDate getMaxDate() {
        return baseMapper.getMaxDate();
    }
}

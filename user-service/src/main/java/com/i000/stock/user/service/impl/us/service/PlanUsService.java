package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.PlanUsMapper;
import com.i000.stock.user.dao.model.PlanUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlanUsService {

    @Autowired
    private PlanUsMapper planUsMapper;

    public boolean hasDate(LocalDate date) {
        return planUsMapper.getGtDateCount(date) > 0;
    }

    public List<PlanUs> findRecommend() {
        LocalDate maxDate = planUsMapper.getMaxDate();
        EntityWrapper<PlanUs> ew = new EntityWrapper<>();
        ew.where("date={0}", maxDate).and().isNotNull("name");
        return planUsMapper.selectList(ew);
    }

    public void updateById(PlanUs planUs) {
        planUsMapper.updateById(planUs);
    }
}

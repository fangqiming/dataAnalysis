package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.i000.stock.user.dao.mapper.HoldUsMapper;
import com.i000.stock.user.dao.model.HoldUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class HoldUsService {

    @Autowired
    private HoldUsMapper holdUsMapper;

    public List<HoldUs> findByDate(LocalDate date) {
        EntityWrapper<HoldUs> ew = new EntityWrapper<>();
        ew.where("new_date={0}", date).isNotNull("code");
        System.out.println(ew.getSqlSegment());
        return holdUsMapper.selectList(ew);
    }

    public BigDecimal getOldPriceByDateAndCode(LocalDate date, String code) {
        return holdUsMapper.getOldPriceByDateAndCode(date, code);
    }

}

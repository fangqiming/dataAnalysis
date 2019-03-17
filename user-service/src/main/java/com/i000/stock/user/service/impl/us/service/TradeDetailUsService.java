package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.TradeDetailUsMapper;
import com.i000.stock.user.dao.model.TradeDetailUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TradeDetailUsService {

    @Autowired
    private TradeDetailUsMapper tradeDetailUsMapper;

    public List<TradeDetailUs> findByDate(LocalDate date) {
        EntityWrapper<TradeDetailUs> ew = new EntityWrapper<>();
        ew.where("new_date={0}", date).isNotNull("code");
        return tradeDetailUsMapper.selectList(ew);
    }
}

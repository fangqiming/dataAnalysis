package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.TradeUsMapper;
import com.i000.stock.user.dao.model.TradeUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TradeUsService {

    @Autowired
    private TradeUsMapper tradeUsMapper;

    public List<TradeUs> findByDate(LocalDate date) {
        EntityWrapper<TradeUs> ew = new EntityWrapper();
        ew.where("date={0}", date).isNotNull("code");
        return tradeUsMapper.selectList(ew);
    }
}

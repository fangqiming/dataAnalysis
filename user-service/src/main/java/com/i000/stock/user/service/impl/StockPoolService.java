package com.i000.stock.user.service.impl;

import com.i000.stock.user.dao.mapper.StockPoolMapper;
import com.i000.stock.user.dao.model.StockPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockPoolService {

    @Autowired
    private StockPoolMapper stockPoolMapper;

    public List<StockPool> findAll() {
        return stockPoolMapper.selectList(null);
    }

}

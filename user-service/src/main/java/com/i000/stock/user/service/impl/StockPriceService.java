package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.i000.stock.user.dao.mapper.StockPriceMapper;
import com.i000.stock.user.dao.model.StockPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StockPriceService {

    @Autowired
    private StockPriceMapper stockPriceMapper;

    public void batchSave(List<StockPrice> stockPrices) {
        if (!CollectionUtils.isEmpty(stockPrices)) {
            stockPriceMapper.truncate();
            for (StockPrice stockPrice : stockPrices) {
                stockPrice.setId(null);
                stockPriceMapper.insert(stockPrice);
            }
        }
    }

    public StockPrice getByCode(String code) {
        if (!StringUtils.isEmpty(code)) {
            code = code.contains(".") ? code.split("\\.")[0] : code;
            EntityWrapper<StockPrice> ew = new EntityWrapper<>();
            ew.where("code={0}", code);
            return getOne(ew);
        }
        return null;
    }

    private StockPrice getOne(Wrapper<StockPrice> ew) {
        List<StockPrice> stockPrices = stockPriceMapper.selectList(ew);
        return CollectionUtils.isEmpty(stockPrices) ? null : stockPrices.get(0);
    }
}

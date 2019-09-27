package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.buiness.PriceService;
import com.i000.stock.user.api.service.external.IndexPriceService;
import com.i000.stock.user.dao.mapper.IndexPriceMapper;
import com.i000.stock.user.dao.model.IndexPrice;
import com.i000.stock.user.dao.model.StockPrice;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:03 2018/6/14
 * @Modified By:
 */
@Component
@Transactional
public class IndexPriceServiceImpl implements IndexPriceService {

    @Autowired
    private IndexPriceMapper indexPriceMapper;

    @Resource
    private PriceService priceService;


    @Override
    public String getContent(String date) {
        if (StringUtils.isNotBlank(date)) {
            return indexPriceMapper.getContentByDate(date);
        }
        return String.format("DATE (%s) error", date);
    }

    @Override
    public void save(IndexPrice indexPrice) {
        indexPriceMapper.insert(indexPrice);
    }

    @Override
    public StringBuffer get() throws IOException {
        return priceService.get();
    }

    @Override
    public List<StockPrice> findStockPrice() {
        return priceService.findStockPrice();
    }

}

package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.IndexPriceService;
import com.i000.stock.user.api.service.PriceService;
import com.i000.stock.user.dao.mapper.IndexPriceMapper;
import com.i000.stock.user.dao.model.IndexPrice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;

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
        if (StringUtils.isNoneBlank(date)) {
            return indexPriceMapper.getContentByDate(date);
        }
        return String.format("date (%s) error", date);
    }

    @Override
    public void save(IndexPrice indexPrice) {
        indexPriceMapper.insert(indexPrice);
    }

    @Override
    public StringBuffer get() throws IOException {
        return priceService.get();
    }

}

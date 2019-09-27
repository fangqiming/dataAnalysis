package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.service.external.CompanyCrawlerService;
import com.i000.stock.user.api.service.util.EmailService;
import com.i000.stock.user.api.service.external.IndexService;
import com.i000.stock.user.api.service.buiness.PriceService;
import com.i000.stock.user.api.entity.bo.Price;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.model.StockPrice;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:43 2018/4/25
 * @Modified By:
 */
@Slf4j
@Service
@Transactional
public class PriceServiceImpl implements PriceService {

    private static final String comma = ",";
    private static final String newLine = "\n";

    @Resource
    private IndexPriceCacheService indexPriceCacheService;

    @Override
    public StringBuffer get() {
        List<IndexInfo> indexInfos = indexPriceCacheService.getIndex(5);
        List<Price> prices = indexPriceCacheService.getPrice(5);
        StringBuffer result = new StringBuffer();
        return result.append(createIndex(indexInfos)).append(createPrice(prices));
    }

    @Override
    public List<StockPrice> findStockPrice() {
        List<IndexInfo> indexInfos = indexPriceCacheService.getIndex(5);
        List<Price> prices = indexPriceCacheService.getPrice(5);
        List<StockPrice> stockPrices = ConvertUtils.listConvert(indexInfos, StockPrice.class, (d, s) -> {
            d.setClose(s.getClose());
        });
        List<StockPrice> result = ConvertUtils.listConvert(prices, StockPrice.class, (d, s) -> {
            d.setClose(s.getPrice());
        });
        result.addAll(stockPrices);
        return result;
    }

    private StringBuffer createPrice(List<Price> prices) {
        StringBuffer result = new StringBuffer();
        if (!CollectionUtils.isEmpty(prices)) {
            for (Price price : prices) {
                result.append(price.getCode()).append(comma)
                        .append(price.getDate()).append(comma)
                        .append(price.getOpen()).append(comma)
                        .append(price.getHigh()).append(comma)
                        .append(price.getLow()).append(comma)
                        .append(price.getPrice()).append(comma)
                        .append(price.getClose()).append(comma)
                        .append(price.getVolume()).append(comma)
                        .append(price.getAmount()).append(newLine);
            }
        }
        return result;
    }

    private StringBuffer createIndex(List<IndexInfo> indexs) {
        List<String> needIndex = Arrays.asList("sh000001", "sh000016", "sz399001", "sz399005", "sz399006");
        StringBuffer result = new StringBuffer();
        if (!CollectionUtils.isEmpty(indexs)) {
            for (IndexInfo indexInfo : indexs) {
                if (needIndex.contains(indexInfo.getCode().replace("\n", ""))) {
                    result.append(indexInfo.getCode().replace("\n", "")).append(comma)
                            .append(indexInfo.getDate()).append(comma)
                            .append(indexInfo.getOpen()).append(comma)
                            .append(indexInfo.getHigh()).append(comma)
                            .append(indexInfo.getLow()).append(comma)
                            .append(indexInfo.getClose()).append(comma)
                            .append(indexInfo.getPreClose()).append(comma)
                            .append(indexInfo.getVolume()).append(comma)
                            .append(indexInfo.getAmount()).append(newLine);
                }
            }
        }
        return result;
    }

}

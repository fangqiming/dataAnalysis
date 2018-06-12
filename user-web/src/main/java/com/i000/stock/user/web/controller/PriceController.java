package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.PriceVos;
import com.i000.stock.user.api.service.IndexService;
import com.i000.stock.user.api.service.PriceService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.dao.model.IndexInfo;
import com.i000.stock.user.dao.model.Price;
import com.i000.stock.user.web.schedule.RealTimeSchedule;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 19:50 2018/4/25
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/price")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PriceController {

    @Resource
    private PriceService priceService;

    @Resource
    private IndexService indexService;

    private static final String comma = ",";
    private static final String newLine = "\r\n";

    @GetMapping("/get_index_price")
    public StringBuffer getIndexPrice() {
        List<IndexInfo> indexInfos = indexService.get();
        List<Price> prices = priceService.findNotLazy();
        StringBuffer result = new StringBuffer();
        return result.append(createIndex(indexInfos)).append(createPrice(prices));
    }

    @Autowired
    private RealTimeSchedule realTimeSchedule;

    @GetMapping("/test")
    public String test() {
        realTimeSchedule.updateCompany();
        return "ok";
    }


    /**
     * 127.0.0.1:8082/price/get_now?code=600000
     *
     * @param code
     * @return
     */
    @GetMapping("/get_now")
    public ResultEntity get_now(@RequestParam String code) {
        Price price = priceService.get(code);
        return Results.newSingleResultEntity(price);
    }

    /**
     * 127.0.0.1:8082/price/get_old?code=600000&date=2018-04-25
     *
     * @param code
     * @param date
     * @return
     */
    @GetMapping("/get_old")
    public ResultEntity get_now(@RequestParam String code, @RequestParam String date) {
        Price price = priceService.get(code, date);
        return Results.newSingleResultEntity(price);
    }

    /**
     * 127.0.0.1:8082/price/find_now_complete
     *
     * @return
     */
    @GetMapping("/find_now_complete")
    public ResultEntity findNowComplete() {
        List<Price> notLazy = priceService.findNotLazy();
        return Results.newListResultEntity(notLazy);
    }

    /**
     * 127.0.0.1:8082/price/find_now_simple
     *
     * @return
     */
    @GetMapping("/find_now_simple")
    public ResultEntity findNowSimple() {
        List<Price> notLazy = priceService.findNotLazy();
        PriceVos result = new PriceVos();
        notLazy.forEach(a -> {
            result.getName().add(a.getName());
            result.getCode().add(a.getCode());
            result.getOpen().add(a.getOpen());
            result.getClose().add(a.getClose());
            result.getVolume().add(a.getVolume());
            result.getHigh().add(a.getHigh());
            result.getLow().add(a.getLow());
            result.getBuy().add(a.getBuy());
            result.getSell().add(a.getSell());
            result.getIsOpen().add(a.getIsOpen());
            result.setDate(a.getDate());
        });
        return Results.newSingleResultEntity(result);
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

package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.entity.bo.Price;
import com.i000.stock.user.api.service.external.CompanyCrawlerService;
import com.i000.stock.user.api.service.external.IndexService;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.api.service.util.EmailService;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.model.IndexValue;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:03 2018/7/11
 * @Modified By:
 */
@Slf4j
@Service
public class IndexPriceCacheServiceImpl implements IndexPriceCacheService {

    @Autowired
    private IndexService indexService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private IndexValueService indexValueService;

    /**
     * 用于获取全部的公司码
     */
    @Resource
    private CompanyCrawlerService companyCrawlerService;

    @Autowired
    private ExternalServiceImpl externalService;


    private static List<IndexInfo> INDEX = new ArrayList<>();

    private static List<Price> PRICE = new ArrayList<>();

    @Override
    public List<IndexInfo> putIndexToCache(Integer tryNumber) {
        INDEX.clear();
        for (int i = 0; i < tryNumber; i++) {
            try {
                List<IndexInfo> indexInfo = indexService.get();
                if (!CollectionUtils.isEmpty(indexInfo)) {
                    INDEX = indexInfo;
                    return indexInfo;
                }
            } catch (Exception e) {
                sleep(2000L);
            }
        }
        if (tryNumber < 100) {
            emailService.sendMail("【千古：指数信息获取失败】", "网络不稳定,指数信息获取失败,请重试", true);
        }
        throw new ServiceException(ApplicationErrorMessage.SERVER_ERROR.getCode(), "指数信息获取失败，请重试");
    }

    @Override
    public List<Price> putPriceToCache(Integer tryNumber) {
        PRICE.clear();
        for (int i = 0; i < tryNumber; i++) {
            try {
                List<Price> result = new ArrayList<>(4000);
                //获取了全部的公司码
                List<String> codes = companyCrawlerService.getCode();
                //将公司码拼接成参数
                List<List<String>> cutList = cutting(codes, 100.0);
                //遍历
                for (List<String> list : cutList) {
                    //将代码拼接到结果中
                    result.addAll(externalService.getPrice(list));
                }
                //一旦出错就全部重试
                if (!CollectionUtils.isEmpty(result)) {
                    PRICE = result;
                    return result;
                }
            } catch (Exception e) {
                sleep(2000L);
            }
        }
        if (tryNumber < 100) {
            emailService.sendMail("【千古：价格信息获取失败】", "网络不稳定,价格信息获取失败,请重试", true);
        }
        throw new ServiceException(ApplicationErrorMessage.SERVER_ERROR.getCode(), "股票价格信息获取失败，请重试");
    }

    @Override
    public List<IndexInfo> getIndex(Integer tryNumber) {
        if (!CollectionUtils.isEmpty(INDEX)) {
            return INDEX;
        }
        return putIndexToCache(tryNumber);
    }

    @Override
    public List<Price> getPrice(Integer tryNumber) {
        if (!CollectionUtils.isEmpty(PRICE)) {
            return PRICE;
        }
        return putPriceToCache(tryNumber);
    }

    @Override
    public void saveIndexValue() {
        List<IndexInfo> indexInfos = getIndex(20);
        IndexValue indexValue = IndexValue.builder().build();
        for (IndexInfo indexInfo : indexInfos) {
            if (!indexInfo.getDate().equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                //当天不是股市交易日，不保存不计算
                return;
            }
            if (indexInfo.getCode().contains("sh000001")) {
                indexValue.setDate(LocalDate.now());
                indexValue.setSh(indexInfo.getClose());
            }
            if (indexInfo.getCode().contains("sh000300")) {
                indexValue.setHs(indexInfo.getClose());
            }
            if (indexInfo.getCode().contains("sz399006")) {
                indexValue.setCyb(indexInfo.getClose());
            }
        }
        indexValueService.save(indexValue);
    }

    private void sleep(Long second) {
        try {
            Thread.sleep(second);
        } catch (InterruptedException e) {
            log.debug("重试发生中断异常");
        }
    }

    private static List<List<String>> cutting(List<String> codes, double num) {
        List<List<String>> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(codes)) {
            int length = (int) Math.ceil(codes.size() / num);
            for (int i = 0; i < length; i++) {
                int end = (i + 1) * (int) num > codes.size() ? codes.size() : (i + 1) * (int) num;
                result.add(codes.subList(i * (int) num, end));
            }
        }
        return result;
    }
}

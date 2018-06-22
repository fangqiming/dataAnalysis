package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.service.CompanyService;
import com.i000.stock.user.api.service.EmailService;
import com.i000.stock.user.api.service.IndexService;
import com.i000.stock.user.api.service.PriceService;
import com.i000.stock.user.api.entity.bo.Price;
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
    private static final String newLine = "\r\n";

    @Resource
    private CompanyService companyService;

    @Autowired
    private ExternalServiceImpl externalService;

    @Resource
    private IndexService indexService;

    @Resource
    private EmailService emailService;


    @Override
    public StringBuffer get() throws IOException {
        //todo 此处需要做重试处理
        List<IndexInfo> indexInfos = getIndexInfo();
        List<Price> prices = findNotLazy();
        StringBuffer result = new StringBuffer();
        return result.append(createIndex(indexInfos)).append(createPrice(prices));
    }

    private List<Price> findNotLazy() throws IOException {
        List<Price> result = new ArrayList<>(4000);
        List<String> codes = companyService.getCode();
        List<List<String>> cutList = cutting(codes, 100.0);
        for (List<String> list : cutList) {
            result.addAll(externalService.getPrice(list));
        }
        return result;
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

    private List<IndexInfo> getIndexInfo() {
        for (int i = 0; i < 5; i++) {
            try {
                List<IndexInfo> indexInfos = indexService.get();
                if (!CollectionUtils.isEmpty(indexInfos)) {
                    return indexInfos;
                }
            } catch (Exception e) {
                sleep(3000L);
            }
        }
        emailService.sendMail("【千古：获取指数的接口异常】", "指数接口重试超过5次仍旧异常请确认网络是否正常", true);
        return null;
    }


    private void sleep(Long second) {
        try {
            Thread.sleep(second);
        } catch (InterruptedException e) {
            log.debug("重试发生中断异常");
        }
    }


}

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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Autowired
    private CompanyServiceImpl companyService;


    private static List<IndexInfo> INDEX = new ArrayList<>();

    private static List<Price> PRICE = new ArrayList<>();


    @Override
    public List<IndexInfo> putIndexToCache(Integer tryNumber) {
        INDEX.clear();
        List<IndexInfo> result = new ArrayList<>();
        for (int i = 0; i < tryNumber; i++) {
            try {
                List<String> indexs = Arrays.asList("000001.XSHG", "000016.XSHG", "000300.XSHG", "399001.XSHE", "399005.XSHE", "399006.XSHE");
                for (String index : indexs) {
                    Price price = companyCrawlerService.getPrice(index);
                    String code = price.getCode().startsWith("0") ? "sh" + price.getCode() : "sz" + price.getCode();
                    IndexInfo tmp = IndexInfo.builder()
                            .code(code)
                            .date(price.getDate())
                            .amount(price.getAmount())
                            .close(price.getPrice())
                            .preClose(price.getClose())
                            .volume(price.getVolume())
                            .open(price.getOpen())
                            .high(price.getHigh())
                            .low(price.getLow())
                            .build();
                    if (Objects.nonNull(tmp)) {
                        result.add(tmp);
                    }
                }
                INDEX = result;
                return result;
            } catch (Exception e) {
                sleep(2000L);
            }
        }
        if (tryNumber < 100) {
            emailService.sendMail("【毕达：指数信息获取失败】", "网络不稳定,指数信息获取失败,请重试", true);
        }
        throw new ServiceException(ApplicationErrorMessage.SERVER_ERROR.getCode(), "指数信息获取失败，请重试");
    }

    @Override
    public List<Price> putPriceToCache(Integer tryNumber) {
        PRICE.clear();
        for (int i = 0; i < tryNumber; i++) {
            try {
                List<Price> result = new ArrayList<>(4000);
                //获取全部公司代码
                List<String> codes = companyCrawlerService.getCode();
//                for (String code : codes) {
//                    Price price = companyCrawlerService.getPrice(code);
//                    if (Objects.nonNull(price)) {
//                        result.add(price);
//                    }
//                }
                for (int c = 0; c < codes.size(); c++) {
                    String code = codes.get(c);
                    Price price = companyCrawlerService.getPrice(code);
                    log.warn("c=" + c + " code=" + code);
                    if (Objects.nonNull(price)) {
                        result.add(price);
                    }
                    System.out.println(c);
                }


                PRICE = result;
                return result;
            } catch (Exception e) {
                sleep(2000L);
            }
        }
        if (tryNumber < 100) {
            emailService.sendMail("【毕达：价格信息获取失败】", "网络不稳定,价格信息获取失败,请重试", true);
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
            if (!isOpenMarket()) {
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

    /**
     * 判定当天是否是股市交易日
     *
     * @return
     */
    @Override
    public boolean isOpenMarket() {
        try {
            List<Price> price = externalService.getPrice(Arrays.asList("600309"));
            System.out.println(price.get(0).getDate());
            return price.get(0).getDate().equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        } catch (Exception e) {
            log.warn("判定当天是否休市异常");
        }
        return true;
    }

    @Override
    public BigDecimal getOnePrice(String param, Integer tryNumber) {
        for (int i = 0; i < tryNumber; i++) {
            try {
                Price onePrice = externalService.getOnePrice(param);
                return onePrice.getPrice();
            } catch (Exception e) {
                System.out.println("重试中");
                sleep(2000L);
            }
        }
        return new BigDecimal("2.65");
    }

    @Override
    public void clearCache() {

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

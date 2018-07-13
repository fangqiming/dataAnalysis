package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.entity.bo.IndexValueBo;
import com.i000.stock.user.api.service.buiness.IndexGainService;
import com.i000.stock.user.api.service.buiness.OffsetPriceService;
import com.i000.stock.user.api.service.external.CompanyCrawlerService;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.api.service.external.IndexPriceService;
import com.i000.stock.user.api.service.external.IndexService;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.dao.model.IndexGain;
import com.i000.stock.user.dao.model.IndexPrice;
import com.i000.stock.user.dao.model.IndexValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:08 2018/6/14
 * @Modified By:
 */
@Slf4j
@Component
public class IndexPriceSchedule {

    @Resource
    private IndexPriceService indexPriceService;

    @Resource
    private IndexService indexService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyCrawlerService companyCrawlerService;

    @Autowired
    private OffsetPriceService offsetPriceService;

    @Autowired
    private IndexPriceCacheService indexPriceCacheService;

    @Autowired
    private IndexValueService indexValueService;

    /**
     * 保存指数价格信息到数据库中
     */
    @Scheduled(cron = "0 35 15 * * ?")
    public void saveIndexPrice() {
        try {
            StringBuffer stringBuffer = indexPriceService.get();
            if (isStockDay(stringBuffer)) {
                IndexPrice indexPrice = IndexPrice.builder().date(LocalDate.now()).content(stringBuffer.toString()).build();
                indexPriceService.save(indexPrice);
            }
        } catch (Exception e) {
            log.error("[SAVE PRICE INDEX ERROR] e=[{}]", e);
        }
    }

    /**
     * 此接口存在问题。。。
     * <p>
     * 计算并保存指数的收益信息
     */
    @Scheduled(cron = "0 02 15 * * ?")
    public void saveIndexValue() {
        List<IndexInfo> indexInfos = indexService.get();
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

    /**
     * 更新公司信息
     */
    @Scheduled(cron = "0 45 15 * * ?")
    public void updateCompany() {
        try {
            Map<String, String> codeName = companyCrawlerService.getCodeName();
            companyService.batchSave(codeName);
        } catch (Exception e) {
            log.warn("公司信息更细失败", e);
        }
    }

    /**
     * 处理拆并股的价格股数问题此处重试了5次
     */
    @Scheduled(cron = "0 35 9 * * ?")
    public void updateAmount() {
        try {
            StringBuffer stringBuffer = indexPriceService.get();
            if (isStockDay(stringBuffer)) {
                offsetPriceService.updateAmount(stringBuffer);
            }
        } catch (IOException e) {
            log.error("处理股票的拆股失败", e);
        }
    }

    /**
     * 将股票的指数信息和价格信息缓存起来()
     */
    @Scheduled(cron = "0 02 15 * * ?")
    public void cacheIndexPrice() {
        indexPriceCacheService.putIndexToCache(101);
        indexPriceCacheService.putPriceToCache(101);
    }


    /**
     * 判断当天是否是股市交易日
     *
     * @param stringBuffer
     * @return
     */
    private boolean isStockDay(StringBuffer stringBuffer) {
        CharSequence charSequence = stringBuffer.subSequence(0, 20);
        String str = charSequence.toString();
        String[] split = str.split(",");
        LocalDate localDates = LocalDate.parse(split[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return LocalDate.now().compareTo(localDates) == 0;
    }
}

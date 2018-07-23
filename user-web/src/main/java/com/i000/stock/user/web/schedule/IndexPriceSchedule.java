package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.entity.bo.IndexValueBo;
import com.i000.stock.user.api.service.buiness.IndexGainService;
import com.i000.stock.user.api.service.buiness.OffsetPriceService;
import com.i000.stock.user.api.service.external.*;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.api.service.util.EmailService;
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
    private EmailService emailService;

    @Autowired
    private StockPledgeService stockPledgeService;

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
     * 将股票的指数信息和价格信息缓存起来()
     */
    @Scheduled(cron = "0 02 15 * * ?")
    public void cacheIndexPrice() {
        indexPriceCacheService.putIndexToCache(101);
        indexPriceCacheService.putPriceToCache(101);
    }

    /**
     * 计算并保存指数的收益信息
     * 此处仅仅作为一个补偿，防止林老师没有提交
     */
    @Scheduled(cron = "0 30 23 * * ?")
    public void saveIndexValue() {
        try {
            indexPriceCacheService.saveIndexValue();
        } catch (Exception e) {
            log.warn("指数信息已经保存", e);
        }

    }

    /**
     * 更新公司信息
     */
    @Scheduled(cron = "0 45 15 * * ?")
    public void updateCompany() {
        try {
            log.warn("-----------公司信息更新中------------");
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
            log.warn("-----------拆并股逻辑处理中------------");
            StringBuffer stringBuffer = indexPriceService.get();
            if (isStockDay(stringBuffer)) {
                offsetPriceService.updateAmount(stringBuffer);
            }
        } catch (IOException e) {
            emailService.sendMail("拆并股处理失败", e.getMessage(), true);
            log.error("处理股票的拆股失败", e);
        }
    }


    /**
     * 每天凌晨触发更新
     */
    @Scheduled(cron = "0 00 02 * * ?")
    public void updateStockPledge() {
        try {
            stockPledgeService.save();
        } catch (Exception e) {
            log.warn("股权质押信息获取失败", e);
            emailService.sendMail("千古【股权质押信息获取失败】", "请相关人员立即处理", true);
            e.printStackTrace();
        }
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

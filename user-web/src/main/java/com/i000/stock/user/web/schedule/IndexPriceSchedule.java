package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.service.external.IndexPriceService;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.dao.model.IndexPrice;
import com.i000.stock.user.dao.model.IndexUs;
import com.i000.stock.user.dao.model.StockPrice;
import com.i000.stock.user.service.impl.FinancialDateService;
import com.i000.stock.user.service.impl.StockPriceService;
import com.i000.stock.user.service.impl.external.NoticeService;
import com.i000.stock.user.service.impl.external.StockChangeService;
import com.i000.stock.user.service.impl.external.macro.MacroService;
import com.i000.stock.user.service.impl.external.material.MaterialPriceService;
import com.i000.stock.user.service.impl.us.service.IndexUSService;
import com.i000.stock.user.web.service.StockFocusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private IndexPriceCacheService indexPriceCacheService;

    @Autowired
    private StockPledgeService stockPledgeService;

    @Autowired
    private IndexUSService indexUSService;

    @Autowired
    private MaterialPriceService materialPriceService;

    @Autowired
    private MacroService macroService;

    @Autowired
    private StockPriceService stockPriceService;

    @Autowired
    private StockFocusService stockFocusService;

    @Autowired
    private FinancialDateService financialDateService;

    /**
     * 保存指数价格信息到数据库中
     */
    @Scheduled(cron = "0 35 15 * * ?")
    public void saveIndexPrice() {
        try {
            StringBuffer stringBuffer = indexPriceService.get();
            if (true) {
                List<StockPrice> stockPrice = indexPriceService.findStockPrice();
                stockPriceService.batchSave(stockPrice);
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
        log.warn("定时器触发更新A股股市数据");
        indexPriceCacheService.putIndexToCache(101);
        indexPriceCacheService.putPriceToCache(101);
        stockFocusService.save("毕达哥拉斯");

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
     * 每天凌晨触发更新
     */
    @Scheduled(cron = "0 00 01 * * ?")
    public void updateStockPledge() {
        try {
            //更新美股财报信息
            financialDateService.save();
            //股权质押信息
            stockPledgeService.save();
        } catch (Exception e) {
            log.warn("股权质押信息获取失败", e);
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 30 16 * * ?")
    public void saveUsIndex() {
        try {
            IndexUs newIndexUs = indexUSService.getNewestFromNet();
            indexUSService.insert(newIndexUs);
        } catch (Exception e) {
            log.warn("美股指数信息插入失败", e);
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 00 02 * * ?")
    public void updateScore() {
        materialPriceService.savePrice();
        noticeService.findByCodes();
        macroService.updateData();
    }

    @Autowired
    private StockChangeService stockChangeService;

    @Scheduled(cron = "0 00 04 * * ?")
    public void updateStockChange() {
        log.info("更新增减持信息");
        stockChangeService.updateStockChange();
    }


}

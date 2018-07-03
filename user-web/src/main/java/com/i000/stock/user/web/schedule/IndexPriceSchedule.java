package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.entity.bo.IndexValueBo;
import com.i000.stock.user.api.service.*;
import com.i000.stock.user.dao.model.IndexGain;
import com.i000.stock.user.dao.model.IndexPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    private IndexGainService indexGainService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyCrawlerService companyCrawlerService;


    /**
     * 每天 3:35收盘的时候将价格指数信息保存到数据库中
     */
    @Scheduled(cron = "0 35 15 * * ?")
    public void saveIndexPrice() {
        try {
            StringBuffer stringBuffer = indexPriceService.get();
            setStockDay(stringBuffer);
            IndexPrice indexPrice = IndexPrice.builder().date(LocalDate.now()).content(stringBuffer.toString()).build();
            //价格保存到数据库中
            indexPriceService.save(indexPrice);
        } catch (Exception e) {
            log.error("[SAVE PRICE INDEX ERROR] e=[{}]", e);
        }
    }

    /**
     * 计算指数的收益信息
     */
    @Scheduled(cron = "0 40 15 * * ?")
    public void saveIndexGain() {
        List<IndexInfo> indexInfos = indexService.get();
        IndexValueBo indexValueBo = IndexValueBo.builder().build();
        for (IndexInfo indexInfo : indexInfos) {
            if (!indexInfo.getDate().equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {
                //当天不是股市交易日，不保存不计算
                return;
            }
            if (indexInfo.getCode().contains("sh000001")) {
                indexValueBo.setDate(LocalDate.now());
                indexValueBo.setSz(indexInfo.getClose());
            }
            if (indexInfo.getCode().contains("sh000300")) {
                indexValueBo.setHs(indexInfo.getClose());
            }
            if (indexInfo.getCode().contains("sz399006")) {
                indexValueBo.setCyb(indexInfo.getClose());
            }
        }
        IndexGain indexGain = indexGainService.calculateIndexInfo(indexValueBo);
        indexGainService.save(indexGain);
    }

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
     * 处理拆并股的问题  此处也是存在问题。报了一个空指针
     */
//    @Scheduled(cron = "0 35 9 * * ?")
//    public void updateAmount() {
//        try {
//            offsetPriceService.updateAmount();
//        } catch (IOException e) {
//            log.error("处理股票的拆股失败", e);
//        }
//    }
    private boolean setStockDay(StringBuffer stringBuffer) {
        CharSequence charSequence = stringBuffer.subSequence(0, 20);
        String str = charSequence.toString();
        String[] split = str.split(",");
        LocalDate localDates = LocalDate.parse(split[1], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(localDates);
        return LocalDate.now().compareTo(localDates) == 0;
    }
}

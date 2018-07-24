package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.original.HoldService;
import com.i000.stock.user.api.service.external.IndexPriceService;
import com.i000.stock.user.api.service.buiness.OffsetPriceService;
import com.i000.stock.user.api.service.original.TradeService;
import com.i000.stock.user.dao.model.Hold;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:53 2018/6/21
 * @Modified By:
 */
@Slf4j
@Component
public class OffsetPriceServiceImpl implements OffsetPriceService {

    @Autowired
    private HoldService holdService;

    @Resource
    private IndexPriceService indexPriceService;

    @Autowired
    private HoldNowServiceImpl holdNowService;

    @Autowired
    private TradeService tradeService;

    @Override
    public void updateAmount(StringBuffer stringBuffer) {
        List<Hold> holds = holdService.findHold();
        Map<String, BigDecimal> result = new HashMap<>(holds.size());
        //更新份数应该是在开盘的时候确认的。也就是在9点31的时候更新份数 ，，推荐之后自行买卖
        if (isValidHold(holds)) {
            String yesterdayStr = indexPriceService.getContent(holds.get(0).getNewDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            List<String> indexs = holds.stream().map(a -> a.getName()).collect(Collectors.toList());
            //获取昨天的收盘价
            Map<String, BigDecimal> yesterday = getYesterday(yesterdayStr, indexs, 5);
            //获取今天的昨天的收盘价
            Map<String, BigDecimal> today = getYesterday(stringBuffer.toString(), indexs, 6);
            for (String stockCode : today.keySet()) {
                //份数=份数*昨天收盘价/今天的昨天收盘价
                //价格=价格*今天的昨天收盘价/昨天收盘价
                //今天的昨天的收盘价  与 昨天的收盘价做比较得到股票的份数是否需要做偏移  此rate=today/yesterday
                result.put(stockCode, today.get(stockCode).divide(yesterday.get(stockCode), 4, BigDecimal.ROUND_HALF_UP));
            }
            //对股票的份额进行更新
            for (Map.Entry<String, BigDecimal> entry : result.entrySet()) {
                if (!entry.getValue().equals(new BigDecimal(1.0000))) {
                    //更新价格
                    tradeService.updatePrice(entry.getKey(), entry.getValue());
                    //更新份数   1/rate
                    holdNowService.updateAmount(
                            new BigDecimal(1).divide(entry.getValue(),4, BigDecimal.ROUND_HALF_UP),
                            entry.getKey(), holds.get(0).getNewDate());
                }
            }
        }
    }

    /**
     * <code,昨天的收盘价>
     *
     * @param yesterday
     * @param indexs
     * @return
     */
    private Map<String, BigDecimal> getYesterday(String yesterday, List<String> indexs, int place) {
        Map<String, BigDecimal> result = new HashMap<>(indexs.size());
        if (!StringUtils.isEmpty(yesterday)) {
            String[] split = yesterday.split("\n");
            for (String str : split) {
                if (!StringUtils.isEmpty(str)) {
                    String[] priceStr = str.split(",");
                    if (indexs.contains(priceStr[0])) {
                        result.put(priceStr[0], new BigDecimal(priceStr[place]));
                    }
                }
            }
        }
        return result;
    }

    private boolean isValidHold(List<Hold> holds) {
        return !(CollectionUtils.isEmpty(holds) || (holds.size() == 1 && StringUtils.isEmpty(holds.get(0).getName())));
    }
}

package com.i000.stock.user.service.impl.us.trade;

import com.i000.stock.user.dao.model.AssetUs;
import com.i000.stock.user.dao.model.HoldNowUs;
import com.i000.stock.user.dao.model.TradeRecordUs;
import com.i000.stock.user.dao.model.TradeUs;
import com.i000.stock.user.service.impl.us.service.HoldNowUsService;
import com.i000.stock.user.service.impl.us.service.TradeRecordUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Cover implements Trade {

    @Autowired
    private HoldNowUsService holdNowUsService;

    @Autowired
    private TradeRecordUsService tradeRecordUsService;

    /**
     * 平仓，->指的买入股票，余额减少
     *
     * @param assetUs
     * @param tradeUs
     * @return
     */
    @Override
    public AssetUs trade(AssetUs assetUs, TradeUs tradeUs, BigDecimal oneShareMoney) {
        List<HoldNowUs> holds = holdNowUsService.findByCode(tradeUs.getCode());
        for (HoldNowUs holdNowUs : holds) {
            assetUs.setBalance(assetUs.getBalance().subtract(holdNowUs.getNewPrice().multiply(holdNowUs.getAmount())));
            TradeRecordUs tradeRecordUs = createTradeRecord(holdNowUs, "COVER");
            tradeRecordUsService.insert(tradeRecordUs);
        }
        List<Long> ids = holds.stream().map(a -> a.getId()).collect(Collectors.toList());
        holdNowUsService.deleteBatchIds(ids);
        return assetUs;
    }
}

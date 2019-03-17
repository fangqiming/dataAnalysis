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
public class Sell implements Trade {

    /**
     * 卖出股票 => 余额增加，删除持仓。
     *
     * @param assetUs
     * @param tradeUs
     * @return
     */

    @Autowired
    private HoldNowUsService holdNowUsService;

    @Autowired
    private TradeRecordUsService tradeRecordUsService;

    @Override
    public AssetUs trade(AssetUs assetUs, TradeUs tradeUs, BigDecimal oneShareMoney) {
        //卖出两次，但是此处传递过来只有一个，那就是全部卖了
        List<HoldNowUs> sellShares = holdNowUsService.findByCode(tradeUs.getCode());
        //此时价格已经是最新的价格了
        for (HoldNowUs holdNowUs : sellShares) {
            assetUs.setBalance(assetUs.getBalance()
                    .add(holdNowUs.getNewPrice().multiply(holdNowUs.getAmount())));
            TradeRecordUs tradeRecordUs = createTradeRecord(holdNowUs, "SELL");
            tradeRecordUsService.insert(tradeRecordUs);
        }
        List<Long> ids = sellShares.stream().map(a -> a.getId()).collect(Collectors.toList());
        holdNowUsService.deleteBatchIds(ids);
        return assetUs;
    }
}

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

@Component
public class Buy implements Trade {

    @Autowired
    private TradeRecordUsService tradeRecordUsService;

    @Autowired
    private HoldNowUsService holdNowUsService;

    /**
     * 买股票,余额减少
     * 账户余额增加，
     *
     * @param assetUs
     * @param tradeUs
     */
    @Override
    public AssetUs trade(AssetUs assetUs, TradeUs tradeUs, BigDecimal oneShareMoney) {
        BigDecimal amount = oneShareMoney.divide(tradeUs.getPrice(), 0, BigDecimal.ROUND_UP);
        //插入当前持仓
        HoldNowUs holdNowUs = HoldNowUs.builder()
                .amount(amount).code(tradeUs.getCode()).name(tradeUs.getName())
                .newDate(tradeUs.getDate()).oldDate(tradeUs.getDate())
                .newPrice(tradeUs.getPrice()).oldPrice(tradeUs.getPrice())
                .type(tradeUs.getType()).user(assetUs.getUser()).build();
        holdNowUsService.insert(holdNowUs);
        //参入交易记录
        TradeRecordUs tradeRecordUs = createTradeRecord(holdNowUs, "BUY");
        tradeRecordUsService.insert(tradeRecordUs);
        assetUs.setBalance(assetUs.getBalance().subtract(holdNowUs.getNewPrice().multiply(holdNowUs.getAmount())));
        return assetUs;
    }
}

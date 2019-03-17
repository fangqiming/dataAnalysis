package com.i000.stock.user.service.impl.us.trade;

import com.i000.stock.user.dao.model.*;

import java.math.BigDecimal;

public interface Trade {

    /**
     * 交易
     *
     * @param assetUs
     * @param tradeUs
     */
    AssetUs trade(AssetUs assetUs, TradeUs tradeUs, BigDecimal oneShareMoney);


    default TradeRecordUs createTradeRecord(HoldNowUs holdNowUs, String type) {
        return TradeRecordUs.builder()
                .code(holdNowUs.getCode())
                .action(type)
                .amount(holdNowUs.getAmount())
                .name(holdNowUs.getName())
                .newDate(holdNowUs.getNewDate())
                .newPrice(holdNowUs.getNewPrice())
                .oldDate(holdNowUs.getOldDate())
                .oldPrice(holdNowUs.getOldPrice())
                .type(holdNowUs.getType())
                .user(holdNowUs.getUser()).build();

    }
}

package com.i000.stock.user.service.impl.us.parse;

import com.i000.stock.user.dao.mapper.TradeDetailUsMapper;
import com.i000.stock.user.dao.model.TradeDetailUs;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TradeDetailShortParse implements ParseTrade {

    private static final String TYPE = "SHORT";

    @Autowired
    private TradeDetailUsMapper tradeDetailUsMapper;

    @Override
    public void save(String[] content, LocalDate date) {
        List<TradeDetailUs> tradeDetailUses = parseToBean(content, TYPE);

        if (!CollectionUtils.isEmpty(tradeDetailUses)) {
            for (TradeDetailUs tradeDetailUs : tradeDetailUses) {
                LocalDate oldDate = tradeDetailUs.getOldDate();
                BigDecimal oldPrice = tradeDetailUs.getOldPrice();
                tradeDetailUs.setOldDate(tradeDetailUs.getNewDate());
                tradeDetailUs.setOldPrice(tradeDetailUs.getNewPrice());
                tradeDetailUs.setNewDate(oldDate);
                tradeDetailUs.setNewPrice(oldPrice);
                tradeDetailUsMapper.insert(tradeDetailUs);
            }
        } else {
            TradeDetailUs tradeDetailUs = TradeDetailUs.builder().newDate(date).build();
            tradeDetailUsMapper.insert(tradeDetailUs);
        }
    }
}

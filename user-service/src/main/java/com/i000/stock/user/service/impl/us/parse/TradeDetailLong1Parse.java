package com.i000.stock.user.service.impl.us.parse;

import com.i000.stock.user.dao.mapper.TradeDetailUsMapper;
import com.i000.stock.user.dao.model.TradeDetailUs;
import com.i000.stock.user.service.impl.us.PatternUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class TradeDetailLong1Parse implements ParseTrade {

    private static final String TYPE = "LONG1";

    @Autowired
    private TradeDetailUsMapper tradeDetailUsMapper;

    @Override
    public void save(String[] content, LocalDate date) {
        List<TradeDetailUs> tradeDetailUses = parseToBean(content, TYPE);
        if (!CollectionUtils.isEmpty(tradeDetailUses)) {
            for (TradeDetailUs tradeDetailUs : tradeDetailUses) {
                tradeDetailUsMapper.insert(tradeDetailUs);
            }
        } else {
            TradeDetailUs tradeDetailUs = TradeDetailUs.builder().newDate(date).build();
            tradeDetailUsMapper.insert(tradeDetailUs);
        }

    }
}

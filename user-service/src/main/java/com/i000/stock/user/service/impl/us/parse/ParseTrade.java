package com.i000.stock.user.service.impl.us.parse;


import com.i000.stock.user.dao.model.TradeDetailUs;
import com.i000.stock.user.service.impl.us.PatternUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface ParseTrade extends Parse {

    /**
     * @param content
     * @param type
     * @return
     */
    default List<TradeDetailUs> parseToBean(String[] content, String type) {
        List<TradeDetailUs> result = new ArrayList<>(content.length);
        for (String line : content) {
            if (PatternUtil.VALID_ITEM.matcher(line).find() || PatternUtil.VALID_ITEM_BLANK.matcher(line).find()) {
                String[] items = line.split(PatternUtil.TAB.pattern());
                if (items.length < 5) {
                    items = line.split(PatternUtil.TWO_BLANK.pattern());
                }
                TradeDetailUs tradeDetailUs = TradeDetailUs.builder()
                        .code(items[0])
                        .oldPrice(new BigDecimal(items[1]))
                        .oldDate(LocalDate.parse(items[2], PatternUtil.DF))
                        .newPrice(new BigDecimal(items[3]))
                        .newDate(LocalDate.parse(items[4], PatternUtil.DF))
                        .name(items[7])
                        .type(type).build();
                result.add(tradeDetailUs);
            }
        }
        return result;
    }
}

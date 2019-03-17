package com.i000.stock.user.service.impl.us.parse;

import com.i000.stock.user.dao.mapper.TradeUsMapper;
import com.i000.stock.user.dao.model.TradeUs;
import com.i000.stock.user.service.impl.us.PatternUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class TodayTradeParse implements Parse {

    @Autowired
    private TradeUsMapper tradeUsMapper;

    /**
     * @param content
     */
    @Override
    public void save(String[] content, LocalDate date) {
        Boolean hasTrade = false;
        for (String line : content) {
            if (PatternUtil.DATE.matcher(line).find()) {
                String[] items = line.split(PatternUtil.TAB.pattern());
                if (items.length < 5) {
                    items = line.split(PatternUtil.TWO_BLANK.pattern());
                }
                String name = items[7].split("\\|")[0];
                TradeUs tradeUs = TradeUs.builder().date(LocalDate.parse(items[0], PatternUtil.DF_SLANT))
                        .type(items[1])
                        .code(items[2])
                        .action(items[3])
                        .price(new BigDecimal(items[4]))
                        .name(name)
                        .note(items[7]).build();
                tradeUsMapper.insert(tradeUs);
                hasTrade = true;
            }
        }
        if (!hasTrade) {
            TradeUs tradeUs = TradeUs.builder().date(date).build();
            tradeUsMapper.insert(tradeUs);
        }
    }
}

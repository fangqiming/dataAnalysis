package com.i000.stock.user.service.impl.us.parse;

import com.i000.stock.user.dao.model.HoldUs;
import com.i000.stock.user.service.impl.us.PatternUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface ParseHold extends Parse {

    /**
     * 将字符串解析为对象
     *
     * @param content
     * @param type
     * @return
     */
    default List<HoldUs> parseToBean(String[] content, String type) {
        List<HoldUs> result = new ArrayList<>(content.length);
        for (String line : content) {
            if (PatternUtil.VALID_ITEM.matcher(line).find() || PatternUtil.VALID_ITEM_BLANK.matcher(line).find()) {
                String[] items = line.split(PatternUtil.TAB.pattern());
                if (items.length < 5) {
                    items = line.split(PatternUtil.TWO_BLANK.pattern());
                }
                HoldUs holdUs = HoldUs.builder().code(items[0]).type(type)
                        .oldPrice(new BigDecimal(items[1]))
                        .oldDate(LocalDate.parse(items[2], PatternUtil.DF))
                        .newPrice(new BigDecimal(items[4]))
                        .newDate(LocalDate.parse(items[5], PatternUtil.DF)).build();
                result.add(holdUs);
            }
        }
        return result;
    }
}

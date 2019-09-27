package com.i000.stock.user.service.impl.us.parse;

import com.i000.stock.user.dao.mapper.HoldUsMapper;
import com.i000.stock.user.dao.model.HoldUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@Component
public class HoldShortUsParse implements ParseHold {

    private static final String TYPE = "SHORT";

    @Autowired
    private HoldUsMapper holdUsMapper;

    @Override
    public void save(String[] content, LocalDate date) {

        List<HoldUs> holdUses = parseToBean(content, TYPE);
        if (!CollectionUtils.isEmpty(holdUses)) {
            for (HoldUs holdUs : holdUses) {
                holdUsMapper.insert(holdUs);
            }
        } else {
            HoldUs holdUs = HoldUs.builder().type(TYPE)
                    .newDate(date).build();
            holdUsMapper.insert(holdUs);
        }

    }
}

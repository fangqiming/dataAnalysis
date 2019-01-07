package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.buiness.FuturesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class FuturesServiceImpl implements FuturesService {

    @Override
    public BigDecimal getPosition(boolean isContainRecommend) {
        return null;
    }

    @Override
    public void shortIf() {

    }

    @Override
    public void coverIf() {

    }

    @Override
    public void forceCoverIf() {

    }
}

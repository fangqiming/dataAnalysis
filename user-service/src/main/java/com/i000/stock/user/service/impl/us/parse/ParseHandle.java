package com.i000.stock.user.service.impl.us.parse;

import com.i000.stock.user.service.impl.us.PatternUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ParseHandle {

    @Autowired
    private HoldLong1UsParse holdLong1UsParse;

    @Autowired
    private HoldLong2UsParse holdLong2UsParse;

    @Autowired
    private HoldShortUsParse holdShortUsParse;

    @Autowired
    private PlanParse planParse;

    @Autowired
    private TodayTradeParse todayTradeParse;

    @Autowired
    private TradeDetailLong1Parse tradeDetailLong1Parse;

    @Autowired
    private TradeDetailLong2Parse tradeDetailLong2Parse;

    @Autowired
    private TradeDetailShortParse tradeDetailShortParse;

    private Map<String, Parse> map = new HashMap<>(8);

    @PostConstruct
    public void init() {
        map.put(PatternUtil.LONG1_HOLD_KEY, holdLong1UsParse);
        map.put(PatternUtil.LONG2_HOLD_KEY, holdLong2UsParse);
        map.put(PatternUtil.SHORT_HOLD_KEY, holdShortUsParse);

        map.put(PatternUtil.LONG1_TRADE_KEY, tradeDetailLong1Parse);
        map.put(PatternUtil.LONG2_TRADE_KEY, tradeDetailLong2Parse);
        map.put(PatternUtil.SHORT_TRADE_KEY, tradeDetailShortParse);

        map.put(PatternUtil.TOMORROW_PLAN_KEY, planParse);
        map.put(PatternUtil.TODAY_TRADE_KEY, todayTradeParse);
    }

    public Parse get(String key) {
        return map.get(key);
    }

}

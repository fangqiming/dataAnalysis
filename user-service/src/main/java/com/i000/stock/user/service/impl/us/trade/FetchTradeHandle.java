package com.i000.stock.user.service.impl.us.trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class FetchTradeHandle {

    @Autowired
    private Buy buy;

    @Autowired
    private Cover cover;

    @Autowired
    private Sell sell;

    @Autowired
    private Short ashort;

    private Map<String, Trade> map = new HashMap<>(4);

    @PostConstruct
    private void init() {
        map.put("BUY", buy);
        map.put("SELL", sell);
        map.put("SHORT", ashort);
        map.put("COVER", cover);
    }

    public Trade getTrade(String key) {
        return map.get(key);
    }
}

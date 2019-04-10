package com.i000.stock.user.service.impl.external;

import com.alibaba.fastjson.JSONObject;
import com.i000.stock.user.api.entity.bo.StockChangeDataBO;
import com.i000.stock.user.api.entity.bo.StockChangeDataListBO;
import com.i000.stock.user.dao.mapper.StockChangeMapper;
import com.i000.stock.user.dao.model.Rank;
import com.i000.stock.user.dao.model.StockChange;
import com.i000.stock.user.service.impl.RankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StockChangeService {

    private final String URL = "http://f10.eastmoney.com/CompanyManagement/CompanyManagementAjax?code=%s";

    @Autowired
    private StockChangeMapper stockChangeMapper;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RankService rankService;

    public List<StockChange> getFromNet(String code) {
        List<StockChange> result = new ArrayList<>();
        try {
            String url = String.format(URL, code.startsWith("6") ? "SH" + code : "SZ" + code);
            String httpResult = restTemplate.getForObject(url, String.class);
            System.out.println(httpResult);
            StockChangeDataListBO stockChangeDataListBO = JSONObject.parseObject(httpResult, StockChangeDataListBO.class);
            List<StockChangeDataBO> stockChangeList = stockChangeDataListBO.getRptShareHeldChangeList();
            for (StockChangeDataBO a : stockChangeList) {
                BigDecimal haveNumber = "--".equals(a.getJcgp()) ? null : new BigDecimal(a.getJcgp());
                BigDecimal tradePrice = "--".equals(a.getJjjj()) ? null : new BigDecimal(a.getJjjj());
                BigDecimal bdsl = "--".equals(a.getBdsl()) ? null : new BigDecimal(a.getBdsl());
                result.add(StockChange.builder().name(a.getBdr()).way(a.getGfbdtj())
                        .tradePrice(tradePrice).occupation(a.getDjgg()).haveNumber(haveNumber)
                        .changeNumber(bdsl).date(LocalDate.parse(a.getRq(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .code(code).build());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public BigDecimal getChangeNumber(String code) {
        return stockChangeMapper.getChangeNumber(code);
    }

    public void updateStockChange() {
        List<Rank> ranks = rankService.finaAll();
        for (Rank rank : ranks) {
            List<StockChange> stockChanges = getFromNet(rank.getCode());
            if (!CollectionUtils.isEmpty(stockChanges)) {
                LocalDate date = stockChangeMapper.getMaxDateByCode(rank.getCode());
                List<StockChange> newChange = stockChanges.stream().filter(a -> Objects.isNull(date) || a.getDate().compareTo(date) > 0).collect(Collectors.toList());
                for (StockChange stockChange : newChange) {
                    stockChangeMapper.insert(stockChange);
                }
            }
            sleep(500L);
        }
    }

    private void sleep(Long mill) {
        try {
            Thread.sleep(mill);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

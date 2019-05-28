package com.i000.stock.user.service.impl.external;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.i000.stock.user.api.entity.bo.StockChangeDataBO;
import com.i000.stock.user.api.entity.util.Cache;
import com.i000.stock.user.dao.mapper.StockChangeMapper;
import com.i000.stock.user.dao.model.Rank;
import com.i000.stock.user.dao.model.StockChange;
import com.i000.stock.user.service.impl.RankService;
import lombok.extern.log4j.Log4j;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.params.CookiePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j
@Service
public class StockChangeService {

    private final String URL = "https://stock.xueqiu.com/v5/stock/f10/cn/skholderchg.json?&symbol=%s&page=1&size=10&extend=true";

    @Autowired
    private StockChangeMapper stockChangeMapper;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RankService rankService;

    private Cache<HttpEntity<MultiValueMap<String, String>>> COOKIE_CACHE;

    public HttpEntity<MultiValueMap<String, String>> getXqCookie() {
        if (Objects.isNull(COOKIE_CACHE) || diff10Min(COOKIE_CACHE.getTime())) {
            HttpEntity<MultiValueMap<String, String>> result = getXqHttpEntity();
            COOKIE_CACHE = new Cache<>();
            COOKIE_CACHE.setTime(LocalDateTime.now());
            COOKIE_CACHE.setData(result);
        }
        return COOKIE_CACHE.getData();
    }

    public Boolean diff10Min(LocalDateTime before) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(before, now);
        long minutes = duration.toMinutes();
        return minutes > 10;
    }

    private HttpEntity<MultiValueMap<String, String>> getXqHttpEntity() {
        try {
            String url = "https://xueqiu.com";
            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(url);
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            int statusCode = httpClient.executeMethod(getMethod);
            Cookie[] cookies = httpClient.getState().getCookies();
            List<String> cookieList = new ArrayList<>();
            for (Cookie c : cookies) {
                cookieList.add(c.toString());
            }
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.COOKIE, cookieList);

            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            return new HttpEntity<>(param, headers);
        } catch (Exception e) {
            log.warn("雪球的cookie获取失败");
        }
        return null;
    }

    public List<StockChange> getFromNet(String code) {
        List<StockChange> result = new ArrayList<>();
        try {
            String api = String.format(URL, code.startsWith("6") ? "SH" + code : "SZ" + code);
            HttpEntity<MultiValueMap<String, String>> cookie = getXqCookie();
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(api, HttpMethod.GET, cookie, JSONObject.class);
            JSONObject body = exchange.getBody();
            JSONArray jsonArray = body.getJSONObject("data").getJSONArray("items");
            List<StockChangeDataBO> stockChangeList = jsonArray.toJavaList(StockChangeDataBO.class);
            for (StockChangeDataBO a : stockChangeList) {
                LocalDate date = LocalDateTime.ofEpochSecond(a.getChg_date().longValue() / 1000, 0, ZoneOffset.of("+8")).toLocalDate();
                result.add(StockChange.builder()
                        .name(a.getShare_changer_name())
                        .tradePrice(a.getTrans_avg_price())
                        .occupation(a.getDuty())
                        .haveNumber(a.getDaily_shares_balance_otd())
                        .changeNumber(a.getChg_shares_num())
                        .date(date)
                        .code(code).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public BigDecimal getChangeNumber(String code) {
        Long changeNumber = stockChangeMapper.getChangeNumber(code);
        Long haveNumber = stockChangeMapper.gethaveNumber(code);
        if (Objects.nonNull(changeNumber) && Objects.nonNull(haveNumber)) {
            if (haveNumber == 0) {
                return new BigDecimal(-100);
            }
            return new BigDecimal(changeNumber / 1.0 / haveNumber * 100).setScale(1, BigDecimal.ROUND_UP);
        }
        return null;
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

package com.i000.stock.user.service.impl.external;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.i000.stock.user.api.entity.bo.NoticeBO;
import com.i000.stock.user.api.service.buiness.HoldNowService;
import com.i000.stock.user.dao.model.HoldNow;
import com.i000.stock.user.dao.model.StockPool;
import com.i000.stock.user.service.impl.StockPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NoticeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StockPoolService stockPoolService;

    @Autowired
    private HoldNowService holdNowService;

    private Pattern FINANCIAL_REPORT = Pattern.compile("20[0-9]{2}年.*(第[一二三四]季度|年度).*报告");

    private static final String URL = "http://data.eastmoney.com/notices/getdata.ashx?StockCode=%s&CodeType=1&PageIndex=1&PageSize=50&jsObj=QmoAXPTD&SecNodeType=0&FirstNodeType=0";

    public List<NoticeBO> getNoticeByCode(String code, Integer days) {
        try {
            String url = String.format(URL, code);
            String resultStr = restTemplate.getForObject(url, String.class);
            String mainStr = resultStr.split(" = ")[1].split(";")[0];
            JSONObject jsonObject = JSON.parseObject(mainStr);
            System.out.println(jsonObject);
            JSONArray data = jsonObject.getJSONArray("data");
            List<NoticeBO> noticeBOS = data.toJavaList(NoticeBO.class);
            //只返回最近5天的公告
            String now = LocalDateTime.now().toLocalDate().minusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<NoticeBO> collect = noticeBOS.stream().filter(a -> a.getNoticedate()
                    .compareTo(now) >= 0).collect(Collectors.toList());
            collect.forEach(a -> {
                String date = a.getNoticedate();
                a.setNoticedate(date.substring(5, 10));
            });
            return collect;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static Map<LocalDate, List<NoticeBO>> CACHE = new HashMap<>();

    /**
     * 批量查找小股池的公告信息
     *
     * @return
     */
    public List<NoticeBO> findByCodes() {
        if (CollectionUtils.isEmpty(CACHE.get(LocalDate.now()))) {
            List<HoldNow> holdNows = holdNowService.find("10000000");
            List<StockPool> stocks = stockPoolService.findAll();
            holdNows.forEach(a -> stocks.add(StockPool.builder().code(a.getName()).build()));
            List<String> codes = stocks.stream()
                    .map(a -> a.getCode()).collect(Collectors.toList());
            findNotice(codes, 1);
        }
        return CACHE.get(LocalDate.now()).stream()
                .filter(a -> FINANCIAL_REPORT.matcher(a.getNoticetitle()).find())
                .collect(Collectors.toList());
    }


    private void findNotice(List<String> codes, Integer days) {
        List<NoticeBO> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(codes)) {
            for (String code : codes) {
                result.addAll(getNoticeByCode(code, days));
                sleep(1);
            }
        }
        CACHE.clear();
        CACHE.put(LocalDate.now(), result);
    }


    private void sleep(Integer second) {
        try {
            Integer mill = second * 1000;
            Thread.sleep(mill);
        } catch (Exception e) {

        }
    }


}

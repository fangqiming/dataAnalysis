package com.i000.stock.user.web.controller;

import com.i000.stock.user.service.impl.external.StockChangeService;
import com.i000.stock.user.service.impl.us.service.IndexUSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private StockChangeService stockChangeService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IndexUSService indexUSService;

    @GetMapping("/notice")
    public Object create() throws Exception {
        List<String> titles = Arrays.asList("格力电器:2019年第一季度报告全文"
                , "北新建材:2019年第一季度报告正文"
                , "格力电器:独立董事对2019年第一季度公司有关情况的独立意见",
                "格力电器:2018年年度审计报告",
                "格力电器:2018年年度报告",
                "603517:绝味食品2019年一季度经营数据公告");

        Pattern pattern = Pattern.compile("20[0-9]{2}年.*(第[一二三四]季度|年度).*报告");
        for (String title : titles) {
            System.out.println(title + " " + pattern.matcher(title).find());
        }
        return "OK";
    }

}

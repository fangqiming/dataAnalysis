package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.AccountBO;
import com.i000.stock.user.api.service.external.CompanyCrawlerService;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.service.impl.AccountAssetService;
import com.i000.stock.user.service.impl.CompanyCrawlerServiceImpl;
import com.i000.stock.user.service.impl.FinancialDateService;
import com.i000.stock.user.web.schedule.IndexPriceSchedule;
import com.i000.stock.user.web.service.StockFocusService;
import com.i000.stock.user.web.service.TencentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private FinancialDateService financialDateService;

    @Autowired
    private IndexPriceSchedule indexPriceSchedule;

    @Autowired
    private CompanyCrawlerServiceImpl companyCrawlerService;

    @Autowired
    private IndexPriceCacheService indexPriceCacheService;

    @Resource
    private AccountAssetService accountAssetService;

    @Resource
    private TencentService tencentService;

    @GetMapping("/search")
    public Object a(@RequestParam String url) throws Exception {
//        return tencentService.getIBResult(url);
        return tencentService.getContext(url);
    }

    @GetMapping("/index")
    public Object object() throws Exception {
//        return accountAssetService.getCurrent("CN");
        return "OK";
    }

}

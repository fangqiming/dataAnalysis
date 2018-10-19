package com.i000.stock.user.web.controller;

import com.alibaba.fastjson.JSON;
import com.i000.stock.user.api.entity.bo.StockPledgeBo;
import com.i000.stock.user.api.entity.vo.StockPledgeVo;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.service.impl.StockPledgeServiceImpl;
import com.i000.stock.user.web.schedule.IndexPriceSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:41 2018/5/8
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

//    @Autowired
//    private StockPledgeService stockPledgeService;

    @Autowired
    private StockPledgeServiceImpl stockPledgeService;

    @GetMapping("/test")
    public Object create(Integer page, Integer size) throws Exception {
//        stockPledgeService.test();
//        BaseSearchVo build = BaseSearchVo.builder().pageNo(page).pageSize(size).build();
//        Page<StockPledgeVo> search = stockPledgeService.search(build, null, null);
//        List<StockPledgeBo> result = new ArrayList<>();
//        for (StockPledgeVo stockPledgeVo : search.getList()) {
//            if (stockPledgeVo.getCode().startsWith("6")) {
//                result.add(StockPledgeBo.builder().code(stockPledgeVo.getCode()+".XSHG").pledge(stockPledgeVo.getRate()).build());
//            } else {
//                result.add(StockPledgeBo.builder().code(stockPledgeVo.getCode()+".XSHE").pledge(stockPledgeVo.getRate()).build());
//            }
//        }
//        return result;

        return null;
    }
}

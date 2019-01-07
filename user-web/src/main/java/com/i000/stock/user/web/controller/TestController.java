package com.i000.stock.user.web.controller;

import cn.zhouyafeng.itchat4j.api.WechatTools;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.dao.model.StockPledge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private StockPledgeService stockPledgeService;

    @GetMapping(path = "/save_pledge")
    private ResultEntity savePledge(@RequestParam("date") String date) throws Exception {
        List<StockPledge> stockPledges = stockPledgeService.save(date);
        //0,000001.XSHE,0.07
        StringBuffer result = new StringBuffer();
        for (StockPledge stockPledge : stockPledges) {
            String temp = stockPledge.getId() + "," + stockPledge.getCode() + "," + stockPledge.getTotal() + "\r\n";
            result.append(temp);
        }
        System.out.println(result);
        return Results.newSingleResultEntity(result);
    }

    @GetMapping(path = "/logout")
    public ResultEntity searchTrade() {
        WechatTools.logout();
        return Results.newNormalResultEntity("status", 200);
    }


}

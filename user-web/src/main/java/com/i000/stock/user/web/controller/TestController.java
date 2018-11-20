package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.test.PledgeVo;
import com.i000.stock.user.api.entity.vo.StockPledgeVo;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.service.impl.ReverseRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @GetMapping("/test")
    public Object create() throws Exception {
//        BaseSearchVo baseSearchVo = BaseSearchVo.builder().pageSize(40000).pageNo(1).build();
//        Page<StockPledgeVo> search = stockPledgeService.search(baseSearchVo, null, null);
//        List<StockPledgeVo> list = search.getList();
//        List<PledgeVo> pledgeVos = ConvertUtils.listConvert(list, PledgeVo.class, (a, b) -> a.setPledge(b.getRate()));
//        return pledgeVos;

//        stockPledgeService.save();
        return "Ok";
    }
}

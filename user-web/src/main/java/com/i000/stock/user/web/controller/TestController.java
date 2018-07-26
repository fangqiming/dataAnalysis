package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.bk.FinancialService;
import com.i000.stock.user.api.service.bk.OtherFormulaService;
import com.i000.stock.user.dao.model.Financial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
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

    @Autowired
    private FinancialService financialService;

    @GetMapping("/test")
    public Object create() throws Exception {
        List<Financial> financials = financialService.find();
        return financials;

    }
}

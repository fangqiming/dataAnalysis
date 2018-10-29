package com.i000.stock.user.web.controller;

import com.i000.stock.user.service.impl.ReverseRepoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
    private ReverseRepoService reverseRepoService;

    @GetMapping("/test")
    public Object create(@RequestParam String str, @RequestParam BigDecimal amount) throws Exception {
        LocalDate date = LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return date;
    }
}

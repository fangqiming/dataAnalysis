package com.i000.stock.user.web.controller;

import com.i000.stock.user.web.service.StockFocusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private StockFocusService stockFocusService;

    @GetMapping("/search")
    public Object a() {
        stockFocusService.save("毕达哥拉斯");
        return "OK";
    }

}

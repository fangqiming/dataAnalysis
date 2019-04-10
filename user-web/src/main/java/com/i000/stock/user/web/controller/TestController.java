package com.i000.stock.user.web.controller;

import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.dao.model.StockChange;
import com.i000.stock.user.service.impl.external.StockChangeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private StockChangeService stockChangeService;

    @GetMapping("/notice")
    public ResultEntity create() {
        List<StockChange> fromNet = stockChangeService.getFromNet("600309");
        return Results.newListResultEntity(fromNet);
    }
}

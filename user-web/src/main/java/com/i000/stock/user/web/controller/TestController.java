package com.i000.stock.user.web.controller;

import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.service.impl.external.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @Autowired
    private NoticeService noticeService;

    @PostMapping("/notice")
    public ResultEntity create(@RequestParam String code) {
//        List<NoticeBO> notice = noticeService.findNotice(code);
//        return Results.newListResultEntity(notice);
        return null;
    }
}

package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
    private EmailService emailService;

    @GetMapping("/test")
    public String create() throws IOException {
        emailService.sendMail("测试邮件", "hahahahaah", true);
        return "haha";
    }
}

package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.bo.KVBo;
import com.i000.stock.user.api.service.CompanyInfoCrawlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    private CompanyInfoCrawlerService companyInfoCrawlerService;


    @GetMapping("/test")
    public String create() throws IOException {
        List<KVBo> info = companyInfoCrawlerService.putCache("600309");
        System.out.println(info);
        return "OK";
    }
}

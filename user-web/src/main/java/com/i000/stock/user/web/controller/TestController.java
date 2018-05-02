package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.api.service.MailFetchService;
import com.i000.stock.user.dao.mapper.HoldMapper;
import com.i000.stock.user.web.schedule.MailSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:31 2018/4/23
 * @Modified By:
 */
@Slf4j
@Controller
public class TestController {


    @Resource
    private MailFetchService mailFetchService;

    @Resource
    private AssetService assetService;

    /**
     * 测试数据库访问
     * 127.0.0.1:8082/test_dao
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/test_dao")
    public Object testDao() throws Exception {
        LocalDate localDate = mailFetchService.initMail();
        System.out.println(System.currentTimeMillis());
//        assetService.calculate(localDate, "root");
        System.out.println(System.currentTimeMillis());
        return "null";
    }

    /**
     * 127.0.0.1:8082/index
     *
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView bankInfo() {
        return new ModelAndView("index");
    }
}

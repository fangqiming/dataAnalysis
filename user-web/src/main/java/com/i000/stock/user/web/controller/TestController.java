package com.i000.stock.user.web.controller;

import com.i000.stock.user.web.schedule.MailSchedule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

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
    private MailSchedule mailSchedule;

    /**
     * 测试数据库访问
     * 127.0.0.1:8082/test_dao
     *
     * @return
     */
    @GetMapping("/test_dao")
    public Object testDao(String date) throws Exception {
        mailSchedule.fetchMail();
        return "hha";
    }

    /**
     * 127.0.0.1:8082/index
     * @return
     */
    @RequestMapping("/index")
    public ModelAndView bankInfo() {
        return  new ModelAndView("index");
    }
}

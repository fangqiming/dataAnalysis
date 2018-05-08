package com.i000.stock.user.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:41 2018/5/8
 * @Modified By:
 */
@Slf4j
@Controller
@RequestMapping("/test")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TestController {

    @GetMapping("/index")
    public ModelAndView create() {
        return new ModelAndView("index");
    }
}

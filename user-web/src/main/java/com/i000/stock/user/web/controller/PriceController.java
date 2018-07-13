package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.external.IndexPriceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 19:50 2018/4/25
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/price")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PriceController {


    @Resource
    private IndexPriceService indexPriceService;

    /**
     * 提供最新的价格指数信息
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/get_index_price")
    public StringBuffer getIndexPrice() throws IOException {
        return indexPriceService.get();
    }

    /**
     * 获取历史价格指数信息
     *
     * @param date
     * @return
     */
    @GetMapping("/get_old_index_price")
    public String getOldIndexPrice(@RequestParam String date) {
        return indexPriceService.getContent(date);
    }
}

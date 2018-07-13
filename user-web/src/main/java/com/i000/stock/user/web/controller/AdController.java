package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.util.AdService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.model.Ad;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:18 2018/4/28
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/ad")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdController {

    @Resource
    private AdService adService;

    /**
     * 127.0.0.1:8082/character/get
     * 通过key获取文案信息
     *
     * @param key
     * @return
     */
    @GetMapping(path = "/get")
    public ResultEntity get(@RequestParam("key") String key) {
        return Results.newSingleResultEntity(adService.get(key));
    }

    /**
     * 127.0.0.1:8082/character/save
     * 保存或者更新文案信息
     *
     * @param ad
     * @return
     */
    @PostMapping(path = "/save")
    public ResultEntity get(@RequestBody Ad ad) {
        ValidationUtils.validate(ad);
        adService.save(ad);
        return Results.newSingleResultEntity(ad);
    }
}

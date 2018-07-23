package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.StockPledgeVo;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:08 2018/7/18
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/data")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DataController {

    @Autowired
    private StockPledgeService stockPledgeService;

    /**
     * 股权质押数据页面
     * 需要获取每天的交易记录
     */
    @GetMapping(path = "/search")
    public ResultEntity searchTrade(BaseSearchVo baseSearchVo, String code,String name) {
        ValidationUtils.validate(baseSearchVo);
        Page<StockPledgeVo> result = stockPledgeService.search(baseSearchVo, code,name);
        return CollectionUtils.isEmpty(result.getList())
                ? Results.newPageResultEntity(0L, new ArrayList<>(0))
                : Results.newPageResultEntity(result.getTotal(), result.getList());
    }

}

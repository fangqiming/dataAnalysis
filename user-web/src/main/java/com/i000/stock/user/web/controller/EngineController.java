package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.buiness.ChooseStockService;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.web.task.DataHandleTask;
import com.i000.stock.user.web.thread.ReceiveRecommendThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.sql.SQLException;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:03 2018/5/8
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/engine")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EngineController {

    @Autowired
    private ReceiveRecommendThread receiveRecommendThread;

    @Autowired
    private DataHandleTask dataHandleTask;

    @Autowired
    private IndexPriceCacheService indexPriceCacheService;

    @Autowired
    private ChooseStockService chooseStockService;

    /**
     * 用于接收推荐信息 注意没有考虑拆股与分红
     *
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping(value = "/receive_recommend")
    public ResultEntity receiveRecommend(@RequestBody String content, @RequestParam(defaultValue = "1") Integer needSave) {
        ValidationUtils.validateParameter(content, "内容不能为空");
        receiveRecommendThread.execute(() -> dataHandleTask.run(content, needSave));
        try {
            indexPriceCacheService.saveIndexValue();
        } catch (Exception e) {
            log.warn("指数价格信息已经被保存", e);
        }
        return Results.newNormalResultEntity("result", "success");
    }

    @GetMapping(value = "/find_stock_by_choose")
    public StringBuffer findCode() {
        return chooseStockService.findCode();
    }
}

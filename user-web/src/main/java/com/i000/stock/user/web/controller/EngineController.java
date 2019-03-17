package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.buiness.ChooseStockService;
import com.i000.stock.user.api.service.util.FileService;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.model.IndexUs;
import com.i000.stock.user.service.impl.RankService;
import com.i000.stock.user.service.impl.us.ParseReportService;
import com.i000.stock.user.service.impl.us.service.IndexUSService;
import com.i000.stock.user.web.task.DataHandleTask;
import com.i000.stock.user.web.thread.ReceiveRecommendThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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

    @Autowired
    private FileService fileService;

    @Autowired
    private IndexUSService indexUSService;

    @Autowired
    private RankService rankService;

    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping(value = "/receive_recommend")
    public ResultEntity receiveRecommend(@RequestBody String content, @RequestParam(defaultValue = "1") Integer needSave) {
        ValidationUtils.validateParameter(content, "内容不能为空");
        receiveRecommendThread.execute(() -> dataHandleTask.run(content, needSave));
        try {
            indexPriceCacheService.saveIndexValue();
        } catch (Exception e) {
            log.warn("指数价格信息已经被保存");
        }
        return Results.newNormalResultEntity("result", "success");
    }

    @Autowired
    private ParseReportService parseReportService;

    @Transactional(rollbackFor = RuntimeException.class)
    @PostMapping(value = "/receive_us_recommend")
    public ResultEntity receiveUsRecommend(@RequestBody String content) {
        ValidationUtils.validateParameter(content, "内容不能为空");
        fileService.saveFile(content, "recommend/us");
        receiveRecommendThread.execute(() -> parseReportService.parse(content));
        try {
            IndexUs newestFromNet = indexUSService.getNewestFromNet();
            indexUSService.insert(newestFromNet);
        } catch (Exception e) {
            log.warn("美股指数价格信息已经被保存");
        }
        return Results.newNormalResultEntity("result", "success");
    }


    @PostMapping("/receive_cn_rank")
    public ResultEntity receiveRank(@RequestBody String content) throws IOException {
        fileService.saveFile(content, "recommend/rank");
        rankService.save(content);
        return Results.newNormalResultEntity("result", "success");
    }

    @GetMapping(value = "/find_stock_by_choose")
    public StringBuffer findCode() {
        return chooseStockService.findCode();
    }


}

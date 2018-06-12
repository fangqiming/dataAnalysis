package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.api.service.UserInfoService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.Hold;
import com.i000.stock.user.dao.model.UserInfo;
import com.i000.stock.user.service.impl.RecommendParseImpl;
import com.i000.stock.user.web.thread.ReceiveRecommendThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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
    private RecommendParseImpl recommendParse;

    @Resource
    private AssetService assetService;

    @Resource
    private HoldService holdService;

    @Resource
    private UserInfoService userInfoService;

    @Autowired
    private ReceiveRecommendThread receiveRecommendThread;


    /**
     * 用于接收推荐信息 注意没有考虑拆股与分红
     *
     * @return
     */
    @PostMapping(value = "/receive_recommend")
    public ResultEntity receiveRecommend(@RequestBody String content) {
        ValidationUtils.validateParameter(content, "内容不能为空");
        log.info(content);

        LocalDate date = recommendParse.parse(content);
        List<Hold> trade = holdService.getTrade();
        List<Hold> holdInit = holdService.findHoldInit(date);
        if (Objects.nonNull(date)) {
            Page<UserInfo> search = userInfoService.search(BaseSearchVo.builder().pageNo(1).pageSize(50).build());
            double ceil = Math.ceil(search.getTotal() / 50.0);
            calculate(search, date, trade, holdInit);
            for (int i = 2; i <= ceil; i++) {
                Page<UserInfo> page = userInfoService.search(BaseSearchVo.builder().pageNo(i).pageSize(50).build());
                calculate(page, date, trade, holdInit);
            }
        }


//        receiveRecommendThread.execute(() -> {
//            LocalDate date = recommendParse.parse(content);
//            List<Hold> trade = holdService.getTrade();
//            List<Hold> holdInit = holdService.findHoldInit(date);
//            if (Objects.nonNull(date)) {
//                Page<UserInfo> search = userInfoService.search(BaseSearchVo.builder().pageNo(1).pageSize(50).build());
//                double ceil = Math.ceil(search.getTotal() / 50.0);
//                calculate(search, date, trade, holdInit);
//                for (int i = 2; i <= ceil; i++) {
//                    Page<UserInfo> page = userInfoService.search(BaseSearchVo.builder().pageNo(i).pageSize(50).build());
//                    calculate(page, date, trade, holdInit);
//                }
//            }
//        });
        return Results.newNormalResultEntity("result", "success");
    }


    private void calculate(Page<UserInfo> page, LocalDate date, List<Hold> trade, List<Hold> holdInit) {
        if (!CollectionUtils.isEmpty(page.getList())) {
            for (UserInfo userInfo : page.getList()) {
                assetService.calculate(date, userInfo.getName(), trade, holdInit);
            }
        }
    }


}

package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.PlanInfoVo;
import com.i000.stock.user.api.entity.vo.PlanVo;
import com.i000.stock.user.api.service.CompanyInfoCrawlerService;
import com.i000.stock.user.api.service.PlanService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.model.Plan;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:24 2018/4/27
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/recommend")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendController {

    @Resource
    private PlanService planService;

    @Resource
    private CompanyInfoCrawlerService companyInfoCrawlerService;

    /**
     * 127.0.0.1:8082/recommend/find
     * 用于获取最新推荐
     *
     * @return
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(path = "/find")
    public ResultEntity find() {
        LocalDate date = planService.getMaxDate();
        List<Plan> plans = planService.findByDate(date);
        return plans.size() == 1 && StringUtils.isBlank(plans.get(0).getName()) ?
                Results.newListResultEntity(new ArrayList<>(0)) :
                Results.newListResultEntity(ConvertUtils.listConvert(plans, PlanVo.class));
    }

    @GetMapping(path = "/get_info")
    public ResultEntity getInfo() {
        LocalDate date = planService.getMaxDate();
        List<Plan> plans = planService.findByDate(date);
        List<PlanInfoVo> result = new ArrayList<>(plans.size());
        for (Plan plan : plans) {
            PlanInfoVo tmp = create(plan.getName(), true);
            tmp.setInfo(companyInfoCrawlerService.getInfo(plan.getName()));
            result.add(tmp);
        }
        if (CollectionUtils.isEmpty(plans)) {
            for (String indexCode : Arrays.asList("sh000001", "sz399006", "sz399300")) {
                PlanInfoVo tmp = create(indexCode, false);
                result.add(tmp);
            }
        }
        return Results.newListResultEntity(result);
    }

    private PlanInfoVo create(String code, boolean needPrefix) {
        String stockCode = needPrefix
                ? (code.startsWith("60") ? "sh" + code : "sz" + code)
                : code;
        return PlanInfoVo.builder()
                .min(String.format("http://image.sinajs.cn/newchart/min/n/%s.gif", stockCode))
                .daily(String.format("http://image.sinajs.cn/newchart/daily/n/%s.gif", stockCode))
                .weekly(String.format("http://image.sinajs.cn/newchart/weekly/n/%s.gif", stockCode))
                .monthly(String.format("http://image.sinajs.cn/newchart/monthly/n/%s.gif", stockCode)).build();
    }

}

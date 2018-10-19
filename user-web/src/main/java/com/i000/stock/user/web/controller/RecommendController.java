package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.PlanInfoVo;
import com.i000.stock.user.api.entity.vo.PlanVo;
import com.i000.stock.user.api.service.buiness.UserInfoService;
import com.i000.stock.user.api.service.external.CompanyInfoCrawlerService;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.api.service.original.PlanService;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.model.Plan;
import com.i000.stock.user.dao.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Resource
    private CompanyService companyService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 127.0.0.1:8082/recommend/find
     * 用于获取最新推荐
     *
     * @return
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(path = "/find")
    public ResultEntity find() {
        String userCode = RequestContext.getInstance().getAmountShare();
        userCode = StringUtils.isBlank(userCode) ? "10000000" : userCode;
        LocalDate date = planService.getMaxDate();
        List<Plan> plans = planService.findByDate(date);
        UserInfo user = userInfoService.getByName(userCode);
        //此处就需要根据请求头信息获取推荐投资资金比
        if (plans.size() == 1 && StringUtils.isBlank(plans.get(0).getName())) {
            return Results.newListResultEntity(new ArrayList<>(0));
        } else {
            List<PlanVo> planVos = ConvertUtils.listConvert(plans, PlanVo.class);
            setNameAndRate(planVos, user);
            return Results.newListResultEntity(planVos);
        }
    }

    @GetMapping(path = "/get_info")
    public ResultEntity getInfo() {
        LocalDate date = planService.getMaxDate();
        List<Plan> plans = planService.findByDate(date);
        List<PlanInfoVo> result = new ArrayList<>(plans.size());
        for (Plan plan : plans) {
            if (StringUtils.isNoneBlank(plan.getName())) {
                PlanInfoVo tmp = create(plan.getName(), true);
                tmp.setInfo(companyInfoCrawlerService.getInfo(plan.getName()));
                result.add(tmp);
            }
        }
        if (CollectionUtils.isEmpty(result)) {
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
                .monthly(String.format("http://image.sinajs.cn/newchart/monthly/n/%s.gif", stockCode))
                .showImg(String.format("http://image.sinajs.cn/newchart/daily/n/%s.gif", stockCode))
                .select("daily")
                .build();
    }

    private void setNameAndRate(List<PlanVo> planVos, UserInfo user) {

        if (!CollectionUtils.isEmpty(planVos)) {
            for (PlanVo planVo : planVos) {
                String stockName = companyService.getNameByCode(planVo.getName());
                planVo.setStockName(stockName);
                planVo.setInvestmentRatio(new BigDecimal(1).divide(user.getInitNum(), 4, BigDecimal.ROUND_UP));
                planVo.setAmount(user.getInitAmount().divide(user.getInitNum(), 4, BigDecimal.ROUND_UP));
            }
        }
    }
}

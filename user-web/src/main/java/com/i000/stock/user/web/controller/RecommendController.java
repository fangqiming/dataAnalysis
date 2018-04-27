package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.BaseLineTrendVO;
import com.i000.stock.user.api.entity.vo.PlanVo;
import com.i000.stock.user.api.entity.vo.RecommendPageVO;
import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.LineService;
import com.i000.stock.user.api.service.PlanService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.CodeEnumUtil;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.LineGroupQuery;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.bo.StepEnum;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Plan;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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
    private LineService lineService;

    @Resource
    private PlanService planService;

    @Resource
    private AssetService assetService;

    /**
     * 127.0.0.1:8082/recommend/get_contrast
     * 用于获取推荐记录中的 大盘与引擎的对比曲线图
     *
     * @param step
     * @return
     */
    @GetMapping(path = "/get_contrast")
    public ResultEntity getBaseLineTrend(@RequestParam String step) {
        StepEnum stepEnum = CodeEnumUtil.transformationStr2Enum(step, StepEnum.class);
        List<LineGroupQuery> baseLines =
                stepIsDay(stepEnum) ?
                        lineService.findBaseLineDay(stepEnum) :
                        lineService.findBaseLineGroup(stepEnum);

        BaseLineTrendVO baseLineTrendVO = new BaseLineTrendVO();
        baseLines.stream().sorted(Comparator.comparing(LineGroupQuery::getTime)).forEach(baseLine -> {
            baseLineTrendVO.getAiMarket().add(baseLine.getAiMarket());
            baseLineTrendVO.getBaseMarket().add(baseLine.getBaseMarket());
            baseLineTrendVO.getTime().add(baseLine.getTime());
        });
        return Results.newSingleResultEntity(baseLineTrendVO);
    }


    /**
     *  127.0.0.1:8082/recommend/find
     * 用于获取最新推荐
     *
     * @return
     */
    @CrossOrigin(origins = "*", maxAge = 3600)
    @GetMapping(path = "/find")
    public ResultEntity find() {
        LocalDate date = planService.getMaxDate();
        List<Plan> byDate = planService.findByDate(date);
        return Results.newListResultEntity(ConvertUtils.listConvert(byDate, PlanVo.class));
    }

    /**
     * 分页查询推荐信息
     * 127.0.0.1:8082/recommend/search
     * @param baseSearchVo
     * @return
     */
    @GetMapping(path = "/search")
    public ResultEntity search(BaseSearchVo baseSearchVo) {
        //1.分页获取获利信息
        ValidationUtils.validate(baseSearchVo);

        Page<Asset> pageData = assetService.search(baseSearchVo);
        if (CollectionUtils.isEmpty(pageData.getList())) {
            return Results.newPageResultEntity(0L, null);
        }
        //需要根据日期查询全部的推荐历史
        List<LocalDate> dataInfo = pageData.getList().stream().map(a -> a.getDate()).collect(toList());
        List<Plan> plans = planService.findByDate(dataInfo);
        Map<LocalDate, List<Plan>> map = plans.stream().collect(groupingBy(Plan::getNewDate));
        List<List<PlanVo>> plan = new ArrayList<>(16);
        map.forEach((k, v) -> plan.add(ConvertUtils.listConvert(v, PlanVo.class)));
        //获取结果
        List<RecommendPageVO> result = new ArrayList<>(pageData.getList().size());
        for (Asset userProfit : pageData.getList()) {
            RecommendPageVO recommendPageVO = RecommendPageVO.builder().date(userProfit.getDate())
                    .gainRate(userProfit.getGain())
                    .recommend(ConvertUtils.listConvert(map.get(userProfit.getDate()), PlanVo.class)).build();
            result.add(recommendPageVO);
        }
        return Results.newPageResultEntity(pageData.getTotal(), result);
    }

    private boolean stepIsDay(StepEnum step) {
        return step.equals(StepEnum.WEEK) || step.equals(StepEnum.MONTH);
    }
}

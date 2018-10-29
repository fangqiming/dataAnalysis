package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.PlanInfoVo;
import com.i000.stock.user.api.entity.vo.PlanVo;
import com.i000.stock.user.api.service.buiness.AssetService;
import com.i000.stock.user.api.service.buiness.HoldNowService;
import com.i000.stock.user.api.service.buiness.UserInfoService;
import com.i000.stock.user.api.service.external.CompanyInfoCrawlerService;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.api.service.original.HoldService;
import com.i000.stock.user.api.service.original.PlanService;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.HoldNow;
import com.i000.stock.user.dao.model.Plan;
import com.i000.stock.user.dao.model.UserInfo;
import com.i000.stock.user.service.impl.ReverseRepoService;
import com.i000.stock.user.service.impl.operate.BuyAssetImpl;
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
import java.util.Objects;

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

    @Resource
    private BuyAssetImpl buyAsset;

    @Resource
    private ReverseRepoService reverseRepoService;

    @Resource
    private AssetService assetService;

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
        Asset asset = assetService.getLately(userCode);

        BigDecimal oneShareMoney = buyAsset.getOneShareMoney(userCode, true);
        BigDecimal shareRate = new BigDecimal(1).divide(user.getInitNum(), 4, BigDecimal.ROUND_UP);

        //代码后续在优化
        if (plans.size() == 1 && StringUtils.isBlank(plans.get(0).getName())) {
            //需要在此处追加对于逆回购的推荐
            ArrayList<PlanVo> result = new ArrayList<>(1);
            PlanVo repo = getRepo(user.getInitNum(), BigDecimal.ZERO, asset.getBalance(), oneShareMoney);
            result.add(repo);
            return Results.newListResultEntity(result);
        } else {
            List<PlanVo> planVos = ConvertUtils.listConvert(plans, PlanVo.class);
            setNameAndRate(planVos, oneShareMoney, shareRate);

            //计划买入的数量
            long buy = planVos.stream().filter(a -> a.getAction().equals("BUY")).count();

            PlanVo repo = getRepo(user.getInitNum(), new BigDecimal(buy), asset.getBalance(), oneShareMoney);
            if (Objects.nonNull(repo)) {
                planVos.add(repo);
            }
            //需要追加逆回购的推荐
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

    private void setNameAndRate(List<PlanVo> planVos, BigDecimal oneMoney, BigDecimal noeRate) {

        if (!CollectionUtils.isEmpty(planVos)) {
            for (PlanVo planVo : planVos) {
                String stockName = companyService.getNameByCode(planVo.getName());
                planVo.setStockName(stockName);
                if("SELL".equals(planVo.getAction())){
                    planVo.setInvestmentRatio(new BigDecimal("1"));
                }else{
                    planVo.setInvestmentRatio(noeRate);
                    planVo.setAmount(oneMoney);
                }
            }
        }
    }

    private PlanVo getRepo(BigDecimal totalShare, BigDecimal share, BigDecimal amount, BigDecimal oneMoney) {

        BigDecimal balance = amount.subtract(share.multiply(oneMoney));
        BigDecimal buyAmount = reverseRepoService.getAmount(balance);
        if (!(buyAmount.compareTo(BigDecimal.ZERO) > 0)) {
            return null;
        }
        Integer holdNum = buyAsset.getHoldNum(true);
        BigDecimal repoShare = totalShare.subtract(share).subtract(new BigDecimal(holdNum));
        return PlanVo.builder().action("SELL").amount(buyAmount).id(100L)
                .investmentRatio(repoShare.divide(totalShare, 4, BigDecimal.ROUND_UP))
                .name("204001").stockName("GC001").type("LONG1").note("国债 | 1天国债回购").build();

    }
}

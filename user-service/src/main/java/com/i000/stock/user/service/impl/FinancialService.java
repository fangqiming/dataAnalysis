package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.bo.*;
import com.i000.stock.user.api.entity.vo.DiagnosisVo;
import com.i000.stock.user.api.entity.vo.FinancialVo;
import com.i000.stock.user.api.entity.vo.TechnologyVo;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.mapper.FinancialMapper;
import com.i000.stock.user.dao.mapper.FinancialNowMapper;
import com.i000.stock.user.dao.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FinancialService {

    private static List<KVBo> KV_TITLES;

    @PostConstruct
    private void init() {
        KV_TITLES = new ArrayList<>();
        KV_TITLES.add(KVBo.builder().k("grossProfitMargin").v("毛利率(%)").build());
        KV_TITLES.add(KVBo.builder().k("netProfitMargin").v("净利率(%)").build());
        KV_TITLES.add(KVBo.builder().k("netProfit").v("净利润(亿)").build());
        KV_TITLES.add(KVBo.builder().k("operatingRevenue").v("营业收入(亿)").build());
        KV_TITLES.add(KVBo.builder().k("incRevenueYearOnYear").v("营业同比增长率(%)").build());
        KV_TITLES.add(KVBo.builder().k("nonOperatingRevenue").v("营业外收入(亿)").build());
        KV_TITLES.add(KVBo.builder().k("accountReceivable").v("应收账款(亿)").build());
        KV_TITLES.add(KVBo.builder().k("inventories").v("存货(亿)").build());
        KV_TITLES.add(KVBo.builder().k("netOperateCashFlow").v("经营活动产生的现金流量净额(亿)").build());
        KV_TITLES.add(KVBo.builder().k("cashAndEquivalentsAtEnd").v("期末的现金流量净额(亿)").build());
        KV_TITLES.add(KVBo.builder().k("cashEquivalentIncrease").v("期末现金流量净增加额(亿)").build());
        KV_TITLES.add(KVBo.builder().k("goodWill").v("商誉(亿)").build());
        KV_TITLES.add(KVBo.builder().k("totalLiability").v("总负债(亿)").build());
        KV_TITLES.add(KVBo.builder().k("totalAssets").v("总资产(亿)").build());
        KV_TITLES.add(KVBo.builder().k("marketCap").v("市值(亿)").build());
        KV_TITLES.add(KVBo.builder().k("roe").v("roe(%)").build());
    }

    @Autowired
    private FinancialMapper financialMapper;

    @Autowired
    private FinancialNowMapper financialNowMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private RankExplainService rankExplainService;

    @Autowired
    private StockPledgeService stockPledgeService;

    public List<Financial> findByCode(String code) {
        EntityWrapper<Financial> ew = new EntityWrapper();
        ew.like("code", code + "%").orderBy("day");
        return financialMapper.selectList(ew);
    }

    public FinancialVo findFinancialByCode(String code) {
        FinancialVo result = new FinancialVo();
        List<Financial> financials = findByCode(code);
        if (!CollectionUtils.isEmpty(financials)) {
            List<String> years = financials.stream().map(a -> a.getDay()
                    .format(DateTimeFormatter.ofPattern("yyyy"))).collect(Collectors.toList());
            years.sort(String::compareTo);
            financials.sort(Comparator.comparing(Financial::getDay));
            result.setTitle(years);
            List<FinancialFiveBo> financialFiveBos = new ArrayList<>();
            for (KVBo item : KV_TITLES) {
                financialFiveBos.add(create(financials, item.getK(), item.getV()));
            }
            result.setFinancial(financialFiveBos);
        }
        return result;
    }

    private FinancialFiveBo create(List<Financial> financials, String property, String titleName) {
        //通过日期排好序
        FinancialFiveBo result = new FinancialFiveBo();
        result.setTitle(titleName);
        financials.sort(Comparator.comparing(Financial::getDay));
        for (int i = 0; i < financials.size(); i++) {
            Financial financial = financials.get(i);
            BigDecimal value = getFieldValueByFieldName(property, financial);
            String key = "k" + i;
            setValue2Object(result, key, value);
        }
        return result;
    }

    private BigDecimal getFieldValueByFieldName(String fieldName, Object object) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            //设置对象的访问权限，保证对private的属性的访问
            field.setAccessible(true);
            return (BigDecimal) field.get(object);
        } catch (Exception e) {
            return null;
        }
    }

    private void setValue2Object(Object object, String property, BigDecimal value) {
        try {
            Field f = object.getClass().getDeclaredField(property);
            f.setAccessible(true);
            f.set(object, value);
        } catch (Exception e) {

        }
    }

    public DiagnosisVo getDiagnosisResult(String code) {
        String codeName = companyService.getNameByCode(code);
        //经营状况
        OperateBo operateBo = createOperateBo(code);
        //财务风险
        FinancialRiskBo financialRiskBo = createFinancialRiskBo(code);
        AIIndexBo aiIndexBo = createAiIndexBo(code);
        return DiagnosisVo.builder().code(code).name(codeName).aiIndexBo(aiIndexBo)
                .financialRiskBo(financialRiskBo).operateBo(operateBo).build();
    }

    public List<TechnologyVo> findTechnologyByCode(String code) {
        try {
            List<TechnologyVo> result = new ArrayList<>();
            String url = String.format("http://doctor.10jqka.com.cn/%s/", code);
            Document doc = Jsoup.connect(url).get();
            Elements techcont = doc.getElementsByClass("techcont");
            if (techcont.size() > 0) {
                Element content = techcont.get(0);
                Elements clearfix = content.getElementsByClass("clearfix");
                for (Element item : clearfix) {
                    Elements dt = item.getElementsByTag("dt");
                    if (dt.size() > 0) {
                        TechnologyVo temp = new TechnologyVo();
                        temp.setTitle(dt.get(0).html());
                        Elements dds = item.getElementsByTag("dd");
                        List<String> values = new ArrayList<>(dds.size());
                        for (Element dd : dds) {
                            values.add(dd.attr("tit"));
                        }
                        temp.setValue(values);
                        result.add(temp);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(ApplicationErrorMessage.NET_DATA_GET_ERROR);
        }
    }

    private AIIndexBo createAiIndexBo(String code) {
        StockRank stockRank = rankExplainService.getRankByCode(code);
        AIIndexBo result = new AIIndexBo();
        if (Objects.nonNull(stockRank)) {
            result.setScore(new BigDecimal(100).subtract(vagueScore(stockRank.getScore())));
            BigDecimal beatRate = rankExplainService.getBeatRateByScore(stockRank.getScore());
            result.setBeat(beatRate);
            result.setResult(getGradeByBeat(stockRank.getScore()));
            result.setDate(stockRank.getDate());
            RankExplain rankExplain = rankExplainService.getRankExplainByScore(stockRank.getScore());
            if (Objects.nonNull(rankExplain)) {
                result.setWinRate(rankExplain.getWinRate());
                result.setMaxProfitRate(rankExplain.getMaxProfit());
                result.setMinProfitRate(rankExplain.getMinProfit());
            }
        }
        return result;
    }

    private String getGradeByBeat(BigDecimal score) {
        //得分低于15分
        if (score.compareTo(new BigDecimal(10)) <= 0) {
            return "买入";
        }
        if (score.compareTo(new BigDecimal(50)) >= 0) {
            return "卖出";
        }
        return "观望";
    }

    private BigDecimal vagueScore(BigDecimal score) {
        BigDecimal multiple = score.divide(new BigDecimal(5), 0, BigDecimal.ROUND_DOWN);
        return (multiple.add(new BigDecimal(1))).multiply(new BigDecimal(5));
    }

    private FinancialRiskBo createFinancialRiskBo(String code) {
        /**
         * 还需要一个表，来更新最新的财务信息。
         */
        StockPledge stockPledge = stockPledgeService.getByCode(code);
        FinancialNow financialNow = getByCode(code);
        FinancialRiskBo financialRiskBo = new FinancialRiskBo();
        if (Objects.nonNull(financialNow)) {
            financialRiskBo.setDate(financialNow.getDay());
            financialRiskBo.setDebtRate(financialNow.getDebtRate());
            financialRiskBo.setGoodwillRate(financialNow.getGoodWillRate());
            financialRiskBo.setNetOperateCashFlowRate(financialNow.getNetOperateCashFlowRate());
        }
        if (Objects.nonNull(stockPledge)) {
            financialRiskBo.setPledgeRate(stockPledge.getRate());
        }
        return financialRiskBo;
    }

    private FinancialNow getByCode(String code) {
        EntityWrapper<FinancialNow> ew = new EntityWrapper();
        ew.like("code", code + "%").orderBy("day");
        List<FinancialNow> financialNows = financialNowMapper.selectList(ew);
        if (CollectionUtils.isEmpty(financialNows)) {
            return null;
        }
        return financialNows.get(0);

    }

    private OperateBo createOperateBo(String code) {
        List<Financial> financials = findByCode(code);
        OperateBo operateBo = new OperateBo();
        List<BigDecimal> grossProfitMargin = financials.stream().filter(a -> Objects.nonNull(a.getGrossProfitMargin()))
                .map(a -> a.getGrossProfitMargin()).collect(Collectors.toList());
        List<BigDecimal> roe = financials.stream().filter(a -> Objects.nonNull(a.getRoe()))
                .map(a -> a.getRoe()).collect(Collectors.toList());
        List<BigDecimal> operatingRevenue = financials.stream().filter(a -> Objects.nonNull(a.getOperatingRevenue()))
                .map(a -> a.getOperatingRevenue().divide(new BigDecimal(100000000), 4, BigDecimal.ROUND_UP))
                .collect(Collectors.toList());
        OptionalDouble gross = grossProfitMargin.stream().mapToDouble(a -> a.doubleValue()).average();
        OptionalDouble roeAvg = roe.stream().mapToDouble(a -> a.doubleValue()).average();
        OptionalDouble operate = operatingRevenue.stream().mapToDouble(a -> a.doubleValue()).average();
        operateBo.setGrossProfitMarginAvg(gross.isPresent() ? new BigDecimal(gross.getAsDouble()) : null);
        operateBo.setRoeAvg(roeAvg.isPresent() ? new BigDecimal(roeAvg.getAsDouble()) : null);
        operateBo.setOperatingRevenueAvg(operate.isPresent() ? new BigDecimal(operate.getAsDouble()) : null);
        operateBo.setGrossProfitMarginGrowthRate(getRate(grossProfitMargin));
        operateBo.setOperatingRevenueGrowthRate(getRate(operatingRevenue));
        operateBo.setRoeGrowthRate(getRate(roe));
        operateBo.setDateRange(createDateRange(financials));
        return operateBo;
    }

    private String createDateRange(List<Financial> financials) {
        if (!CollectionUtils.isEmpty(financials)) {
            return financials.get(0).getDay().format(DateTimeFormatter.ofPattern("yyyy")) + "--" +
                    financials.get(financials.size() - 1).getDay().format(DateTimeFormatter.ofPattern("yyyy"));
        }
        return null;
    }

    private BigDecimal getRate(List<BigDecimal> list) {
        List<Double> doubleList = list.stream().map(a -> a.doubleValue()).collect(Collectors.toList());
        MacdBo roeMacd = getMACD(doubleList, 3, 5, 4);
        if (Objects.isNull(roeMacd)) {
            return null;
        }
        return roeMacd.getMacd();
    }

    private static MacdBo getMACD(List<Double> list, final int shortPeriod, final int longPeriod, int midPeriod) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<Double> diffList = new ArrayList<>();
        Double shortEMA;
        Double longEMA;
        Double dif = 0.0;
        Double dea;

        for (int i = list.size() - 1; i >= 0; i--) {
            List<Double> sublist = list.subList(0, list.size() - i);
            shortEMA = getEXPMA(sublist, shortPeriod);
            longEMA = getEXPMA(sublist, longPeriod);
            dif = shortEMA - longEMA;
            diffList.add(dif);
        }
        dea = getEXPMA(diffList, midPeriod);
        return MacdBo.builder().dea(new BigDecimal(dea))
                .dif(new BigDecimal(dif))
                .macd(new BigDecimal((dif - dea) * 2)).build();
    }

    private static Double getEXPMA(final List<Double> list, final int number) {
        Double k = 2.0 / (number + 1.0);
        Double ema = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            ema = list.get(i) * k + ema * (1 - k);
        }
        return ema;
    }

}

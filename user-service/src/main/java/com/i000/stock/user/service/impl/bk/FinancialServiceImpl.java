package com.i000.stock.user.service.impl.bk;

import com.i000.stock.user.api.service.bk.CompositeFormulaService;
import com.i000.stock.user.api.service.bk.FinancialService;
import com.i000.stock.user.api.service.bk.OtherFormulaService;
import com.i000.stock.user.api.service.bk.SingleFormulaService;
import com.i000.stock.user.api.service.buiness.ChooseStockService;
import com.i000.stock.user.dao.bo.FinancialCompositeBo;
import com.i000.stock.user.dao.mapper.FinancialMapper;
import com.i000.stock.user.dao.model.ChooseStock;
import com.i000.stock.user.dao.model.CompositeFormula;
import com.i000.stock.user.dao.model.Financial;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:45 2018/7/25
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class FinancialServiceImpl implements FinancialService {

    @Resource
    private ChooseStockService chooseStockService;
    @Resource
    private SingleFormulaService singleFormulaService;
    @Resource
    private CompositeFormulaService compositeFormulaService;
    @Resource
    private FinancialMapper financialMapper;
    @Resource
    private OtherFormulaService otherFormulaService;

    /**
     * 简单公式的计算结果
     *
     * @param codes
     * @return
     */
    private List<Financial> findBySingle(List<String> codes) {
        Map<String, String> expressions = singleFormulaService.findSql();
        List<Financial> resultTmp = new ArrayList<>(codes.size() * expressions.size());
        List<Financial> result = new ArrayList<>(codes.size());
        for (Map.Entry<String, String> entry : expressions.entrySet()) {
            resultTmp.addAll(financialMapper.findBySingleSql(entry.getValue(), entry.getKey(), codes));
        }
        //单一指标 获取到了之后需要将两个total做加法，此时涉及到显示谁的其它指标值
        Map<String, List<Financial>> financialMap = resultTmp.stream().collect(Collectors.groupingBy(Financial::getCode));
        financialMap.forEach((k, v) -> {
            if (!CollectionUtils.isEmpty(v)) {
                List<Financial> financialList = v.stream().sorted((a, b) -> a.getDay().compareTo(b.getCode())).collect(Collectors.toList());
                BigDecimal total = financialList.stream().map(Financial::getTotal).reduce(BigDecimal.ZERO, (a, b) -> (a.add(b)));
                financialList.get(0).setTotal(total);
                result.add(financialList.get(0));
            }
        });
        return result;
    }

    /**
     * 增长率公式的计算结果
     *
     * @param codes
     * @return
     */
    private Map<String, BigDecimal> findByComposite(List<String> codes) {
        List<CompositeFormula> allComposite = compositeFormulaService.findAll();
        Map<String, BigDecimal> result = new HashMap<>();
        if (!CollectionUtils.isEmpty(allComposite)) {
            for (CompositeFormula compositeFormula : allComposite) {
                List<FinancialCompositeBo> financialBo =
                        financialMapper.findByComposite(compositeFormula.getExpression(), codes, createList(compositeFormula.getDays()));
                Map<String, List<FinancialCompositeBo>> collect = financialBo.stream().collect(Collectors.groupingBy(FinancialCompositeBo::getCode));
                for (Map.Entry<String, List<FinancialCompositeBo>> entry : collect.entrySet()) {
                    BigDecimal value = calculateIndexValue(entry.getValue(), compositeFormula.getWeight(), compositeFormula.getMaxValue());
                    if (result.containsKey(entry.getKey())) {
                        result.put(entry.getKey(), result.get(entry.getKey()).add(value));
                    } else {
                        result.put(entry.getKey(), value);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<Financial> find() {
        //1获取全部的自选股
        List<ChooseStock> allStock = chooseStockService.findAll();
        List<String> codes = allStock.stream().map(ChooseStock::getCode).collect(Collectors.toList());
        List<Financial> result = findBySingle(codes);
        Map<String, BigDecimal> compositeValue = findByComposite(codes);

        Map<String, BigDecimal> otherValue = otherFormulaService.find(codes);
        result.forEach(financial -> {
            financial.setTotal(financial.getTotal()
                    .add(Objects.isNull(compositeValue.get(financial.getCode())) ? BigDecimal.ZERO : compositeValue.get(financial.getCode()))
                    .add(Objects.isNull(otherValue.get(financial.getCode())) ? BigDecimal.ZERO : otherValue.get(financial.getCode())));
        });
        result.sort((a, b) -> b.getTotal().compareTo(a.getTotal()));
        return result;
    }


    private BigDecimal calculateIndexValue(List<FinancialCompositeBo> financialCompositeBos, String weights, BigDecimal max) {
        BigDecimal result = BigDecimal.ZERO;
        List<String> weightList = createList(weights);
        List<FinancialCompositeBo> financial = financialCompositeBos.stream().sorted((a, b) -> (b.getValue().compareTo(a.getValue()))).collect(Collectors.toList());
        for (int i = 0; i < financial.size() - 1; i++) {
            //后一个除以前一个就是 增长率
            BigDecimal growthRate = getGrowthRate(financial.get(i).getValue(), financial.get(i + 1).getValue());
            BigDecimal weight = i > weightList.size() - 1 ? new BigDecimal(weightList.get(weightList.size() - 1)) : new BigDecimal(weightList.get(i));
            result = (result.add(growthRate.multiply(weight))).multiply(new BigDecimal(100));
        }
        return result.compareTo(max) < 0 ? result : max;
    }

    private BigDecimal getGrowthRate(BigDecimal after, BigDecimal before) {
        return before.compareTo(BigDecimal.ZERO) == 0
                ? BigDecimal.ZERO
                : after.subtract(before).divide(before, 4, BigDecimal.ROUND_HALF_UP);
    }

    private List<String> createList(String days) {
        if (!StringUtils.isEmpty(days)) {
            String[] split = days.split(",");
            return Arrays.asList(split);
        }
        return new ArrayList<>(0);
    }

}

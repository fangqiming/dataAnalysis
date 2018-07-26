package com.i000.stock.user.service.impl.bk;

import com.i000.stock.user.api.service.bk.OtherFormulaService;
import com.i000.stock.user.api.service.buiness.ChooseStockService;
import com.i000.stock.user.dao.bo.FinancialCompositeBo;
import com.i000.stock.user.dao.mapper.OtherFormulaMapper;
import com.i000.stock.user.dao.model.ChooseStock;
import com.i000.stock.user.dao.model.OtherFormula;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:01 2018/7/26
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class OtherFormulaServiceImpl implements OtherFormulaService {

    @Resource
    private OtherFormulaMapper otherFormulaMapper;

    @Resource
    private ChooseStockService chooseStockService;

    @Override
    public Map<String, BigDecimal> find(List<String> codes) {
        Map<String, BigDecimal> result = new HashMap<>();
        List<OtherFormula> otherFormulas = otherFormulaMapper.selectList(null);
        if (!CollectionUtils.isEmpty(otherFormulas)) {
            List<FinancialCompositeBo> resultTmp = new ArrayList<>(otherFormulas.size() * codes.size());
            for (OtherFormula otherFormula : otherFormulas) {
                resultTmp.addAll(otherFormulaMapper.find(limitMaxValue(otherFormula.getExpression(), otherFormula.getMaxValue()),
                        otherFormula.getTableName(), codes));
            }
            Map<String, List<FinancialCompositeBo>> valueMap = resultTmp.stream()
                    .collect(Collectors.groupingBy(FinancialCompositeBo::getCode));
            valueMap.forEach((k, v) -> result.put(k, v.stream().map(FinancialCompositeBo::getValue).reduce(BigDecimal.ZERO, (a, b) -> (a.add(b)))));
        }
        return result;
    }


    private String limitMaxValue(String expression, BigDecimal maxValue) {
        return String.format("LEAST(%s,%s) ", expression, maxValue);
    }
}

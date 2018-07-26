package com.i000.stock.user.service.impl.bk;

import com.i000.stock.user.api.service.bk.SingleFormulaService;
import com.i000.stock.user.dao.mapper.SingleFormulaMapper;
import com.i000.stock.user.dao.model.SingleFormula;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:06 2018/7/25
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class SingleFormulaServiceImpl implements SingleFormulaService {

    @Resource
    private SingleFormulaMapper singleFormulaMapper;


    @Override
    public Map<String, String> findSql() {

        List<SingleFormula> singleFormulas = singleFormulaMapper.selectList(null);
        Map<String, String> result = new HashMap<>(singleFormulas.size());
        Map<String, List<SingleFormula>> formulaByData = singleFormulas.stream()
                .collect(Collectors.groupingBy(SingleFormula::getDay));
        for (Map.Entry<String, List<SingleFormula>> entry : formulaByData.entrySet()) {
            String tmp = "";
            for (SingleFormula singleFormula : entry.getValue()) {
                tmp += createSql(singleFormula);
            }
            if (StringUtils.isNoneBlank(tmp)) {
                result.put(entry.getKey(), "(" + tmp.substring(0, tmp.length() - 2) + ")");
            }
        }
        return result;
    }

    private String createSql(SingleFormula singleFormula) {
        return String.format("LEAST(%s,%s) + ", singleFormula.getExpression(), singleFormula.getMaxValue());
    }
}

package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.FinancialNowMapper;
import com.i000.stock.user.dao.model.FinancialNow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialNowService {

    @Autowired
    private FinancialNowMapper financialNowMapper;

    public List<FinancialNow> findByCode(List<String> codes) {
        List<String> param = fromateCode(codes);
        EntityWrapper<FinancialNow> ew = new EntityWrapper<>();
        ew.in("code", param);
        List<FinancialNow> financialNows = financialNowMapper.selectList(ew);
        financialNows.stream().forEach(a -> a.setCode(a.getCode().split("\\.")[0]));
        return financialNows;
    }


    private List<String> fromateCode(List<String> code) {
        if (!CollectionUtils.isEmpty(code)) {
            if (!code.get(0).contains("XS")) {
                List<String> result = new ArrayList<>(code.size());
                for (String temp : code) {
                    if (temp.startsWith("6")) {
                        result.add(temp + ".XSHG");
                    } else {
                        result.add(temp + ".XSHE");
                    }
                }
                return result;
            }
        }
        return code;
    }
}

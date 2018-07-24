package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.buiness.ChooseStockService;
import com.i000.stock.user.dao.mapper.ChooseStockMapper;
import com.i000.stock.user.dao.model.ChooseStock;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:56 2018/7/24
 * @Modified By:
 */
@Component
@Transactional
public class ChooseStockServiceImpl implements ChooseStockService {

    private static String NEW_LINE = "\n";

    @Resource
    private ChooseStockMapper chooseStockMapper;

    @Override
    public StringBuffer findCode() {
        List<ChooseStock> chooseStocks = chooseStockMapper.selectList(null);
        StringBuffer result = new StringBuffer();
        for (ChooseStock chooseStock : chooseStocks) {
            result.append(chooseStock.getCode()).append(NEW_LINE);
        }
        return result;
    }
}

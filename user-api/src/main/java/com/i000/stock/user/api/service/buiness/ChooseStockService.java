package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.dao.model.ChooseStock;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:55 2018/7/24
 * @Modified By:
 */
public interface ChooseStockService {

    StringBuffer findCode();

    List<ChooseStock> findAll();
}

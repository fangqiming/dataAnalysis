package com.i000.stock.user.api.service.original;

import com.i000.stock.user.dao.bo.LineGroupQuery;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:26 2018/4/27
 * @Modified By:
 */
public interface LineService {
    /**
     * 查询出全部的指数值
     *
     * @return
     */
    List<LineGroupQuery> find();
}

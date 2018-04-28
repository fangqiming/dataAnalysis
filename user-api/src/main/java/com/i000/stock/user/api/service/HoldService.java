package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.model.Hold;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:53 2018/4/28
 * @Modified By:
 */
public interface HoldService {

    /**
     * 获取用户的当前持股信息
     *
     * @return
     */
    List<Hold> findHold();
}

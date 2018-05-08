package com.i000.stock.user.api.service;

import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.UserInfo;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:24 2018/5/2
 * @Modified By:
 */
public interface UserInfoService {

    /**
     * 根据名称获取用户信息
     *
     * @param name
     * @return
     */
    UserInfo getByName(String name);

    /**
     * 分页查找用户信息
     *
     * @param baseSearchVo
     * @return
     */
    Page<UserInfo> search(BaseSearchVo baseSearchVo);

    /**
     * 注册
     */
    UserInfo register(UserInfo userInfo);

    /**
     * 登录
     *
     * @param userInfo
     * @return
     */
    UserInfo login(UserInfo userInfo);
}

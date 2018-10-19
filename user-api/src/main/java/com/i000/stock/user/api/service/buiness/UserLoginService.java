package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.UserInfo;
import com.i000.stock.user.dao.model.UserLogin;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:48 2018/10/10
 * @Modified By:
 */
public interface UserLoginService {

    /**
     * 根据名称获取用户信息
     *
     * @param name
     * @return
     */
    UserLogin getByPhone(String name);


    /**
     * 注册
     */
    UserLogin register(UserLogin userLogin);

    /**
     * 登录
     *
     * @param userInfo
     * @return
     */
    UserLogin login(UserLogin userInfo);
}

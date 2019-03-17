package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.api.entity.constant.AuthEnum;
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

    /**
     * 为该用户生成用户码
     *
     * @param userLogin
     * @return
     */
    UserLogin createAccessCode(UserLogin userLogin);

    /**
     * 通过访问码校验权限
     *
     * @param accessCode 访问码
     * @param authEnum   权限码
     */
    void checkAuth(String accessCode, AuthEnum authEnum);
}

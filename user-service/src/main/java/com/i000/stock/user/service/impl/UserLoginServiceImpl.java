package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.buiness.UserLoginService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.mapper.UserInfoMapper;
import com.i000.stock.user.dao.mapper.UserLoginMapper;
import com.i000.stock.user.dao.model.UserInfo;
import com.i000.stock.user.dao.model.UserLogin;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:49 2018/10/10
 * @Modified By:
 */
@Component
@Transactional
public class UserLoginServiceImpl implements UserLoginService {

    @Resource
    private UserLoginMapper userLoginMapper;


    @Override
    public UserLogin getByPhone(String name) {
        return userLoginMapper.getByPhone(name);
    }

    @Override
    public UserLogin register(UserLogin userLogin) {
        //需要检查电话号码是否重复
        Integer countByPhone = userLoginMapper.getCountByPhone(userLogin.getPhone());
        if (countByPhone > 0) {
            throw new ServiceException(ApplicationErrorMessage.USER_HAS_EXIST);
        }
        Integer countByName = userLoginMapper.getCountByName(userLogin.getName());
        if (countByName > 0) {
            throw new ServiceException(ApplicationErrorMessage.USER_HAS_EXIST.getCode(), "用户名已经存在");
        }
        userLogin.setCreateTime(LocalDateTime.now());
        userLoginMapper.insert(userLogin);
        return userLogin;
    }

    @Override
    public UserLogin login(UserLogin userInfo) {
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        UserLogin user;
        if (pattern.matcher(userInfo.getName()).matches()) {
            user = userLoginMapper.getByPhone(userInfo.getName());
        } else {
            user = userLoginMapper.getByName(userInfo.getName());
        }
        if (Objects.isNull(user)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "用户名或手机号不存在");
        }
        if (!userInfo.getPassword().equals(user.getPassword())) {
            throw new ServiceException(ApplicationErrorMessage.PASSWORD_ERROR.getCode(), "密码错误");
        }
        return user;
    }
}

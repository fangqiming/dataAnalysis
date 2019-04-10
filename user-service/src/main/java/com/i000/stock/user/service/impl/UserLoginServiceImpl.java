package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.constant.AuthEnum;
import com.i000.stock.user.api.service.buiness.UserLoginService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.core.util.MD5Factory;
import com.i000.stock.user.dao.mapper.UserLoginMapper;
import com.i000.stock.user.dao.model.UserLogin;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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

    /**
     * 登录60分钟后超时失效
     */
    private static Integer TIME_OUT_MIN = 60 * 24 * 7;

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

    @Override
    public UserLogin createAccessCode(UserLogin userLogin) {
        long time = System.currentTimeMillis();
        String key = String.format("%s_%s", userLogin.getPassword(), time);
        String md5 = MD5Factory.getMD5(key);
        userLogin.setAccessCode(md5 + userLogin.getId());
        userLogin.setLoginTime(LocalDateTime.now());
        userLoginMapper.updateById(userLogin);
        return userLogin;
    }

    @Override
    public void checkAuth(String accessCode, AuthEnum authEnum) {
        UserLogin userLogin = getByAccessCode(accessCode);
        Duration between = Duration.between(userLogin.getLoginTime(), LocalDateTime.now());
        long diffMinute = between.toMinutes();
        if (diffMinute >= TIME_OUT_MIN) {
            throw new ServiceException(ApplicationErrorMessage.ACCESS_CODE_TIME_OUT);
        }
        if (Objects.nonNull(userLogin.getAuthority())) {
            if (!userLogin.getAuthority().contains(authEnum.getValue())) {
                throw new ServiceException(ApplicationErrorMessage.NO_AUTH);
            }
        } else {
            throw new ServiceException(ApplicationErrorMessage.NO_AUTH);
        }
    }

    @Override
    public boolean hasAuth(String accessCode, AuthEnum authEnum) {
        try {
            checkAuth(accessCode, authEnum);
            return true;
        } catch (ServiceException e) {
            return false;
        }
    }

    private UserLogin getByAccessCode(String accessCode) {
        EntityWrapper<UserLogin> ew = new EntityWrapper<>();
        ew.where("access_code = {0}", accessCode);
        List<UserLogin> userLogins = userLoginMapper.selectList(ew);
        if (CollectionUtils.isEmpty(userLogins) || userLogins.size() > 1) {
            throw new ServiceException(ApplicationErrorMessage.ACCESS_CODE_IS_INVALID);
        }
        return userLogins.get(0);
    }


}

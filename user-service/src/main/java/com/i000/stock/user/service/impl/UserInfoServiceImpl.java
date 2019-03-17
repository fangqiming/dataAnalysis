package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.buiness.UserInfoService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.UserInfoMapper;
import com.i000.stock.user.dao.model.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:25 2018/5/2
 * @Modified By:
 */
@Component
@Transactional
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo getByName(String name) {
        return userInfoMapper.getByName(name);
    }

    @Override
    public PageResult<UserInfo> search(BaseSearchVo baseSearchVo) {
        baseSearchVo.setStart();
        List<UserInfo> search = userInfoMapper.search(baseSearchVo);
        PageResult<UserInfo> result = new PageResult<>();
        result.setList(search);
        result.setTotal(userInfoMapper.pageTotal());
        return result;
    }

    @Override
    public UserInfo register(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
        return userInfo;
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        UserInfo byName = userInfoMapper.getByName(userInfo.getName());
        if (Objects.isNull(byName)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "用户名不存在");
        }
        if (!userInfo.getPassword().equals(byName.getPassword())) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "密码错误");
        }
        return byName;
    }
}

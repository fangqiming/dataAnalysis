package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.UserInfoService;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.mapper.UserInfoMapper;
import com.i000.stock.user.dao.model.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    public Page<UserInfo> search(BaseSearchVo baseSearchVo) {
        baseSearchVo.setStart();
        List<UserInfo> search = userInfoMapper.search(baseSearchVo);
        Page<UserInfo> result = new Page<>();
        result.setList(search);
        result.setTotal(userInfoMapper.pageTotal());
        return result;
    }
}

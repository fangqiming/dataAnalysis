package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.UserInfoUsMapper;
import com.i000.stock.user.dao.model.UserInfoUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class UserInfoUsService {

    @Autowired
    private UserInfoUsMapper userInfoUsMapper;

    public UserInfoUs getByUser(String user) {
        EntityWrapper<UserInfoUs> ew = new EntityWrapper<>();
        ew.where("user = {0}", user);
        List<UserInfoUs> userInfoUs = userInfoUsMapper.selectList(ew);
        if (CollectionUtils.isEmpty(userInfoUs)) {
            return null;
        }
        return userInfoUs.get(0);
    }

}

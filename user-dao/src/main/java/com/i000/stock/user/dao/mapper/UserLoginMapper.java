package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.UserLogin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:47 2018/10/10
 * @Modified By:
 */
public interface UserLoginMapper extends BaseMapper<UserLogin> {

    @Select("select count(*) from user_login where `phone` = #{phone}")
    Integer getCountByPhone(String phone);

    @Select("select count(*) from user_login where `name` = #{name}")
    Integer getCountByName(String name);

    /**
     * 根据name查询到用户信息
     *
     * @param phone
     * @return
     */
    UserLogin getByPhone(@Param("phone") String phone);

    /**
     * 根据用户名查询到用户信息
     *
     * @param name
     * @return
     */
    UserLogin getByName(@Param("name") String name);
}

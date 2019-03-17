package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.UserStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserStockMapper extends BaseMapper<UserStock> {

    @Select("select * from user_stock where user=#{user} and `code`=#{code}")
    UserStock getByUserAndCode(@Param("user") String user, @Param("code") String code);

    @Select("select count(*) from user_stock where user=#{user}")
    Long getStockCountByUser(@Param("user") String user);

    @Select("select * from user_stock where user=#{user}")
    List<UserStock> findByUser(@Param("user") String user);

}

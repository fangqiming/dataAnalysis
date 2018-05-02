package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:15 2018/5/2
 * @Modified By:
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    /**
     * 根据name查询到用户信息
     *
     * @param name
     * @return
     */
    UserInfo getByName(@Param("name") String name);

    /**
     * 分页获取用户信息
     *
     * @param baseSearchVo
     * @return
     */
    List<UserInfo> search(@Param("baseSearchVo") BaseSearchVo baseSearchVo);

    /**
     * 获取分页条数
     *
     * @return
     */
    @Select("select found_rows()")
    Long pageTotal();
}

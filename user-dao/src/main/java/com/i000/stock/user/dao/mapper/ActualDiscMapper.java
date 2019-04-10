package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.ActualDisc;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ActualDiscMapper extends BaseMapper<ActualDisc> {

    @Select("select count(*) from actual_disc where `name` =#{name}")
    Long getCountByName(@Param("name") String name);

}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Cpi;
import org.apache.ibatis.annotations.Select;

public interface CpiMapper extends BaseMapper<Cpi> {

    @Select("truncate cpi")
    void truncate();
}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Ppi;
import org.apache.ibatis.annotations.Select;

public interface PpiMapper extends BaseMapper<Ppi> {

    @Select("truncate ppi")
    void truncate();
}

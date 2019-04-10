package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Pmi;
import org.apache.ibatis.annotations.Select;

public interface PmiMapper extends BaseMapper<Pmi> {

    @Select("truncate pmi")
    void truncate();

}

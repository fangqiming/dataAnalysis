package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.StockFocus;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface StockFocusMapper extends BaseMapper<StockFocus> {

    @Select("truncate stock_focus")
    void truncate();
}

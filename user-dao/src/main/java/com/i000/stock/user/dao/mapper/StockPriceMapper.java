package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.StockPrice;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface StockPriceMapper extends BaseMapper<StockPrice> {

    /**
     * 清空表
     */
    @Select("truncate stock_price")
    void truncate();
}

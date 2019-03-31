package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.EverydayStock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

public interface EverydayStockMapper extends BaseMapper<EverydayStock> {

    @Update("update everyday_stock set new_price = #{close} where code = #{code}")
    void updateCloseByCode(@Param("close") BigDecimal close, @Param("code") String code);

}

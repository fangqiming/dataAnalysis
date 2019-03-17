package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.AssetUs;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface AssetUsMapper extends BaseMapper<AssetUs> {

    @Select("select avg((stock - cover)/(balance + cover + stock)*100) from asset_us where `user`=#{user}")
    BigDecimal getAvgPositionByUser(@Param("user") String user);
}

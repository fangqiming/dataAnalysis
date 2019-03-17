package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Rank;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface RankMapper extends BaseMapper<Rank> {

    @Delete("truncate rank")
    Long truncate();

    @Select("select count(*) from rank where score >= #{score}")
    BigDecimal getGtCount(@Param("score") BigDecimal score);

    @Select("select count(*) from rank")
    BigDecimal getCount();

    @Select("select * from rank where code =#{code}")
    Rank getByCode(@Param("code") String code);

}

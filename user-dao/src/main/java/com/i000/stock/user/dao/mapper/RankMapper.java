package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.StockRank;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

public interface RankMapper extends BaseMapper<StockRank> {

    @Delete("truncate `stock_rank`")
    Long truncate();

    @Select("select count(*) from `stock_rank` where score >= #{score}")
    BigDecimal getGtCount(@Param("score") BigDecimal score);

    @Select("select count(*) from `stock_rank`")
    BigDecimal getCount();

    @Select("select * from `stock_rank` where code =#{code}")
    StockRank getByCode(@Param("code") String code);

    @Select("select count(*) from `stock_rank` where score >= #{low} and score < #{high}")
    BigDecimal getScoreRangeCount(@Param("low") Integer low, @Param("high") Integer high);

    @Select("select avg(score) from `stock_rank` ")
    BigDecimal getAvgScore();

}

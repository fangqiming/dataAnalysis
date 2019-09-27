package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.IndexPrice;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:58 2018/6/14
 * @Modified By:
 */
public interface IndexPriceMapper extends BaseMapper<IndexPrice> {

    /**
     * 根据日期获取股市价格
     *
     * @param date
     * @return
     */
    @Select("select content from index_price where date=#{date}")
    String getContentByDate(@Param("date") String date);

}

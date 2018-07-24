package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.StockPledge;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:04 2018/7/17
 * @Modified By:
 */
public interface StockPledgeMapper extends BaseMapper<StockPledge> {

    @Delete("truncate stock_pledge")
    void truncate();

    List<StockPledge> search(@Param("baseSearchVo") BaseSearchVo baseSearchVo, @Param("code") String code, @Param("name") String name);

    @Select("select found_rows()")
    Long pageTotal();
}

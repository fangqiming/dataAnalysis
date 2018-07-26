package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.FinancialCompositeBo;
import com.i000.stock.user.dao.model.Financial;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:05 2018/7/25
 * @Modified By:
 */
public interface FinancialMapper extends BaseMapper<Financial> {

    List<Financial> findBySingleSql(@Param("expression") String expression, @Param("day") String day, @Param("code") List<String> code);

    List<FinancialCompositeBo> findByComposite(@Param("index") String index, @Param("code") List<String> code, @Param("days") List<String> days);

}

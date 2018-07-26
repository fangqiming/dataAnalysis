package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.FinancialCompositeBo;
import com.i000.stock.user.dao.model.OtherFormula;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:58 2018/7/26
 * @Modified By:
 */
public interface OtherFormulaMapper extends BaseMapper<OtherFormula> {

    /**
     * 目前是根据股权质押率计算出质押率公式的值
     * 但是不排除有别的公式会参与进来
     *
     * @param expression
     * @param tableName
     * @param codes
     * @return
     */
    List<FinancialCompositeBo> find(@Param("expression") String expression, @Param("tableName") String tableName, @Param("codes") List<String> codes);
}

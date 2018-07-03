package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.Company;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:00 2018/4/25
 * @Modified By:
 */
public interface CompanyMapper extends BaseMapper<Company> {

    /**
     * 清空表结构
     *
     * @return
     */
    @Delete("truncate company")
    Long truncate();

    /**
     * 通过股票代码获取公司名称
     *
     * @param code
     * @return
     */
    @Select("select `name` from company where `code`=#{code}")
    String getNameByCode(@Param("code") String code);
}

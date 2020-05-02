package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.FinancialDate;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialDateMapper extends BaseMapper<FinancialDate> {

    @Select("truncate financial_date")
    void truncate();
}

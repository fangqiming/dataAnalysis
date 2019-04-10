package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.NewStockAccount;
import org.apache.ibatis.annotations.Select;

public interface NewStockAccountMapper extends BaseMapper<NewStockAccount> {

    @Select("truncate new_stock_account")
    void truncate();

}

package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.InvestorLogBo;
import com.i000.stock.user.dao.model.InvestorLog;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface InvestorLogMapper extends BaseMapper<InvestorLog> {

    @Select("select name,sum(share) as share ,sum(amount) as amount from investor_log GROUP BY name")
    List<InvestorLogBo> findSummary();
}

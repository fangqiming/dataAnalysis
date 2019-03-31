package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.vo.InvestorLogVO;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.InvestorLogBo;
import com.i000.stock.user.dao.mapper.InvestorLogMapper;
import com.i000.stock.user.dao.model.InvestorLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class InvestorLogService {


    @Autowired
    private InvestorLogMapper investorLogMapper;

    public void save(InvestorLog investorLog) {
        investorLogMapper.insert(investorLog);
    }


    /**
     * 查询总的投资金额
     */
    public List<InvestorLogVO> findSummary() {
        List<InvestorLogBo> summary = investorLogMapper.findSummary();
        if (!CollectionUtils.isEmpty(summary)) {
            BigDecimal allShare = summary.stream().filter(a -> Objects.nonNull(a.getShare()))
                    .map(a -> a.getShare()).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
            BigDecimal allAmount = summary.stream().filter(a -> Objects.nonNull(a.getAmount()))
                    .map(a -> a.getAmount()).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
            summary.add(InvestorLogBo.builder().name("总计").share(allShare).amount(allAmount).build());
        }
        return ConvertUtils.listConvert(summary, InvestorLogVO.class);
    }

    /**
     * 根据投资人主键，查询投资详情
     */
    public List<InvestorLog> findByName(String name) {
        EntityWrapper<InvestorLog> ew = new EntityWrapper<>();
        ew.where("name={0}", name);
        return investorLogMapper.selectList(ew);
    }

}

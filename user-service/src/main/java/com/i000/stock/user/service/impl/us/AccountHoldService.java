package com.i000.stock.user.service.impl.us;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.bo.AccountAssetBO;
import com.i000.stock.user.dao.mapper.AccountAssetMapper;
import com.i000.stock.user.dao.mapper.AccountHoldMapper;
import com.i000.stock.user.dao.model.AccountHold;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class AccountHoldService {

    @Resource
    private AccountHoldMapper accountHoldMapper;

    /**
     * 获取指定账户在指定日期的持仓信息
     *
     * @return
     */
    public List<AccountHold> findHold(String accountName, LocalDate date) {
        EntityWrapper<AccountHold> ew = new EntityWrapper<>();
        ew.where("date={0}", date).and("account_name={0}", accountName);
        return accountHoldMapper.selectList(ew);
    }

    public List<AccountHold> findBetween(LocalDate start, LocalDate end) {
        EntityWrapper<AccountHold> ew = new EntityWrapper<>();
        ew.between("date", start, end);
        return accountHoldMapper.selectList(ew);
    }

}

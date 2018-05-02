package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.HoldNowService;
import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.dao.mapper.HoldNowMapper;
import com.i000.stock.user.dao.model.Hold;
import com.i000.stock.user.dao.model.HoldNow;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:19 2018/5/2
 * @Modified By:
 */
@Component
public class HoldNowServiceImpl implements HoldNowService {

    @Resource
    private HoldService holdService;

    @Resource
    private HoldNowMapper holdNowMapper;

    @Override
    public void save(HoldNow holdNow) {
        holdNowMapper.insert(holdNow);
    }

    @Override
    public HoldNow getByNameDateType(String userCode, String name, LocalDate date, String type) {
        return holdNowMapper.getByNameDateType(userCode, name, date, type);
    }

    @Override
    public Integer deleteById(Long id) {
        return holdNowMapper.deleteById(id);
    }

    @Override
    public List<HoldNow> find(String userCode) {
        return holdNowMapper.find(userCode);
    }

    @Override
    public Integer updatePrice(LocalDate date) {
        int result = 0;
        List<Hold> holds = holdService.findHold();
        for (Hold hold : holds) {
            holdNowMapper.updatePrice(hold.getNewPrice(), hold.getName(), date);
            result++;
        }
        return result;
    }
}

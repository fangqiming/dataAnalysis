package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.dao.mapper.HoldMapper;
import com.i000.stock.user.dao.model.Hold;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:54 2018/4/28
 * @Modified By:
 */
@Component
public class HoldServiceImpl implements HoldService {

    @Resource
    private HoldMapper holdMapper;

    @Override
    public List<Hold> findHold() {
        LocalDate maxDate = holdMapper.getMaxDate();
        return Objects.isNull(maxDate) ? new ArrayList<>(0) :
                holdMapper.findByDate(maxDate);
    }
}

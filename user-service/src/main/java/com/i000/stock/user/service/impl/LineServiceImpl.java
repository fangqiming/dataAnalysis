package com.i000.stock.user.service.impl;

import com.i000.stock.user.dao.bo.StepEnum;
import com.i000.stock.user.dao.bo.LineGroupQuery;
import com.i000.stock.user.api.service.LineService;
import com.i000.stock.user.dao.mapper.LineMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:27 2018/4/27
 * @Modified By:
 */
@Component
@Transactional
public class LineServiceImpl implements LineService {

    @Resource
    private LineMapper lineMapper;

    @Override
    public List<LineGroupQuery> findBaseLineDay(StepEnum step) {
        return lineMapper.findByStepDay(step);
    }

    @Override
    public List<LineGroupQuery> findBaseLineGroup(StepEnum step) {
        return lineMapper.findByStepGroup(step);
    }
}

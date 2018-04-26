package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.dao.mapper.AssetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:34 2018/4/26
 * @Modified By:
 */
@Component
public class AssetServiceImpl implements AssetService {

    @Resource
    private AssetMapper assetMapper;

    @Override
    public void calculate(LocalDate date) {

    }

    @Override
    public BigDecimal getGain(LocalDate date) {
        return null;
    }

    @Override
    public BigDecimal getGain(LocalDate start, LocalDate end) {
        return null;
    }
}

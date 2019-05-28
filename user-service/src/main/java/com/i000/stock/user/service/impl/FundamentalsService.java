package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.FundamentalsMapper;
import com.i000.stock.user.dao.model.Fundamentals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class FundamentalsService {

    @Autowired
    private FundamentalsMapper fundamentalsMapper;

    public Fundamentals getByCode(String code) {
        EntityWrapper<Fundamentals> ew = new EntityWrapper<>();
        ew.where("code = {0}", code);
        return getOne(ew);
    }

    private Fundamentals getOne(EntityWrapper<Fundamentals> ew) {
        List<Fundamentals> fundamentals = fundamentalsMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(fundamentals)) {
            return fundamentals.get(0);
        }
        return null;
    }
}

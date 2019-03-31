package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.IndustryChainMapper;
import com.i000.stock.user.dao.model.IndustryChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class IndustryChainService {

    @Autowired
    private IndustryChainMapper industryChainMapper;

    public void save(IndustryChain industryChain) {
        industryChainMapper.insert(industryChain);
    }

    public IndustryChain getByName(String name) {
        if (name.contains("MDI")) {
            name = "MDI";
        }
        EntityWrapper<IndustryChain> ew = new EntityWrapper<>();
        ew.where("name = {0}", name);
        return selectOne(ew);
    }


    private IndustryChain selectOne(EntityWrapper<IndustryChain> ew) {
        List<IndustryChain> industryChains = industryChainMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(industryChains)) {
            return industryChains.get(0);
        }
        return null;
    }
}

package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.MaterialCompanyMapper;
import com.i000.stock.user.dao.mapper.MaterialMapper;
import com.i000.stock.user.dao.model.MaterialCompany;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class MaterialCompanyService {

    @Autowired
    private MaterialCompanyMapper materialCompanyMapper;

    public void save(MaterialCompany materialCompany) {
        materialCompanyMapper.insert(materialCompany);
    }

    public MaterialCompany getByName(String name) {
        EntityWrapper<MaterialCompany> ew = new EntityWrapper<>();
        ew.where("name = {0}", name);
        return selectOne(ew);
    }


    private MaterialCompany selectOne(EntityWrapper<MaterialCompany> ew) {
        List<MaterialCompany> materialCompanies = materialCompanyMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(materialCompanies)) {
            return materialCompanies.get(0);
        }
        return null;
    }

    public List<MaterialCompany> getMaterialByCode(String code) {
        EntityWrapper<MaterialCompany> ew = new EntityWrapper<>();
        ew.like("company_code", code);
        return materialCompanyMapper.selectList(ew);
    }
}

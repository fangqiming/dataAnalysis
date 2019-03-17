package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.dao.mapper.CompanyMapper;
import com.i000.stock.user.dao.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:48 2018/7/3
 * @Modified By:
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;


    @Override
    public void batchSave(Map<String, String> info) {
        if (!CollectionUtils.isEmpty(info)) {
            //先清空
            companyMapper.truncate();
            for (Map.Entry<String, String> entry : info.entrySet()) {
                Company company = new Company();
                company.setCode(entry.getKey());
                company.setName(entry.getValue());
                companyMapper.insert(company);
            }
        }
    }

    @Override
    public String getNameByCode(String code) {
        return companyMapper.getNameByCode(code);
    }

    @Override
    public List<Company> findByName(String name) {
        EntityWrapper<Company> we = new EntityWrapper<>();
        we.like("name", "%" + name + "%");
        return companyMapper.selectList(we);
    }

    @Override
    public List<Company> findAll() {
        return companyMapper.selectList(null);
    }

    @Override
    public List<Company> findByCodes(List<String> codes) {
        EntityWrapper<Company> ew = new EntityWrapper();
        ew.in("code", codes);
        return companyMapper.selectList(ew);
    }

}

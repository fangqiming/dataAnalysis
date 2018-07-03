package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.CompanyService;
import com.i000.stock.user.dao.mapper.CompanyMapper;
import com.i000.stock.user.dao.model.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

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
}

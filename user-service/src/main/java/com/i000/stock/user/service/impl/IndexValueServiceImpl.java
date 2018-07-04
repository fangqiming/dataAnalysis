package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.IndexValueService;
import com.i000.stock.user.dao.mapper.IndexValueMapper;
import com.i000.stock.user.dao.model.IndexValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:10 2018/7/4
 * @Modified By:
 */
@Service
public class IndexValueServiceImpl implements IndexValueService {

    @Autowired
    private IndexValueMapper indexValueMapper;

    @Override
    public List<IndexValue> findAll() {
        return indexValueMapper.selectList(null);
    }
}

package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexBo;
import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.service.IndexService;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 19:16 2018/4/25
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ExternalServiceImpl externalService;

    @Override
    public List<IndexInfo> get() {
        IndexBo index = externalService.getIndex();
        if (!Objects.isNull(index)) {
            return index.getData();
        }
        return new ArrayList<>(0);
    }




}

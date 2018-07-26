package com.i000.stock.user.service.impl.bk;

import com.i000.stock.user.api.service.bk.CompositeFormulaService;
import com.i000.stock.user.dao.mapper.CompositeFormulaMapper;
import com.i000.stock.user.dao.model.CompositeFormula;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:40 2018/7/25
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class CompositeFormulaServiceImpl implements CompositeFormulaService {

    @Resource
    private CompositeFormulaMapper compositeFormulaMapper;

    @Override
    public List<CompositeFormula> findAll() {
        return compositeFormulaMapper.selectList(null);
    }
}

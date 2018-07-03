package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexValueBo;
import com.i000.stock.user.api.entity.bo.PageIndexValueBo;
import com.i000.stock.user.api.entity.vo.GainVo;
import com.i000.stock.user.api.service.IndexGainService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.mapper.IndexGainMapper;
import com.i000.stock.user.dao.model.IndexGain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description: 计算没有问题，使用时需要将数据初始化
 * @Date:Created in 14:35 2018/7/3
 * @Modified By:
 */
@Service
public class IndexGainServiceImpl implements IndexGainService {

    @Autowired
    private IndexGainMapper indexGainMapper;

    @Override
    public void save(IndexGain indexGain) {
        indexGainMapper.insert(indexGain);
    }

    @Override
    public IndexGain calculateIndexInfo(IndexValueBo indexValueBo) {
        //查询出第一个实体
        IndexGain old = indexGainMapper.getFirstIndexGain();
        if (Objects.isNull(old)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "指数数据没有初始化");
        }
        IndexGain last = indexGainMapper.getLastIndexGain();
        //用来计算总的收益率
        return IndexGain.builder().date(indexValueBo.getDate())
                .sz(indexValueBo.getSz())
                .hs(indexValueBo.getHs())
                .cyb(indexValueBo.getCyb())
                .szTotal(getRate(indexValueBo.getSz(), old.getSz()))
                .hsTotal(getRate(indexValueBo.getHs(), old.getHs()))
                .cybTotal(getRate(indexValueBo.getCyb(), old.getCyb()))
                .szGain(getRate(indexValueBo.getSz(), last.getSz()))
                .hsGain(getRate(indexValueBo.getHs(), last.getHs()))
                .cybGain(getRate(indexValueBo.getCyb(), last.getCyb())).build();
    }


    @Override
    public List<IndexGain> find() {
        return indexGainMapper.find();
    }

    @Override
    public IndexGain getDiff(LocalDate date, Integer diff) {
        return indexGainMapper.getDiff(date, diff);
    }

    @Override
    public PageIndexValueBo getDiffGain(LocalDate date, Integer diff) {
        IndexGain last = indexGainMapper.getLastIndexGain();
        IndexGain old = getDiff(date, diff);
        return PageIndexValueBo.builder().szGain(getRate(last.getSz(), old.getSz()))
                .hsGain(getRate(last.getHs(), old.getHs()))
                .cybGain(getRate(last.getCyb(), old.getCyb())).build();
    }


    private BigDecimal getRate(BigDecimal current, BigDecimal old) {
        BigDecimal diff = current.subtract(old);
        return diff.divide(old, 5, BigDecimal.ROUND_UP);
    }
}

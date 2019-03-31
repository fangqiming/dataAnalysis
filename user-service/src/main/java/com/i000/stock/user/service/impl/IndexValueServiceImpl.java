package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.dao.mapper.IndexValueMapper;
import com.i000.stock.user.dao.model.IndexValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;
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

    @Override
    public List<IndexValue> findBetween(LocalDate start, LocalDate end) {
        return indexValueMapper.findBetween(start, end);
    }

    @Override
    public IndexValue getRecently(LocalDate date) {
        return indexValueMapper.getLately(date);
    }

    @Override
    public IndexValue getRecentlyByGt(LocalDate date) {
        return indexValueMapper.getLatelyByGt(date);
    }

    @Override
    public IndexValue getRecentlyByLt(LocalDate date) {
        return indexValueMapper.getLatelyByLt(date);
    }

    @Override
    public IndexValue getLately() {
        return indexValueMapper.getNewest();
    }

    @Override
    public void save(IndexValue indexValue) {
        indexValueMapper.insert(indexValue);
    }

    @Override
    public IndexValue getYearFirst(String year) {
        return indexValueMapper.getYearFirst(year);
    }

    @Override
    public List<IndexValue> getLatelyTwo() {
        return indexValueMapper.getLatelyTwo();
    }

    @Override
    public IndexValue getLastOne() {
        return indexValueMapper.getLastOne();
    }

    @Override
    public IndexValue getBefore(LocalDate date) {
        return indexValueMapper.getBefore(date);
    }

    @Override
    public List<IndexValue> findByDates(List<LocalDate> list) {
        EntityWrapper<IndexValue> ew = new EntityWrapper<>();
        ew.in("date", list);
        return indexValueMapper.selectList(ew);
    }


}

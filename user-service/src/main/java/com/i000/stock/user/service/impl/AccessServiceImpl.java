package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.AccessService;
import com.i000.stock.user.dao.mapper.AccessMapper;
import com.i000.stock.user.dao.model.Access;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:17 2018/5/3
 * @Modified By:
 */
@Component
@Transactional
public class AccessServiceImpl implements AccessService {

    @Resource
    private AccessMapper accessMapper;

    @Override
    public void save(Access access) {
        accessMapper.insert(access);
    }

    @Override
    public Integer getNum(LocalDateTime start, LocalDateTime end) {
        return accessMapper.getNumByDate(start, end);
    }

    @Override
    public Integer getNum(String country, LocalDateTime start, LocalDateTime end) {
        return accessMapper.getNumByCountry(start, end, country);
    }

    @Override
    public Integer getNumByCity(String city, LocalDateTime start, LocalDateTime end) {
        return accessMapper.getNumByCity(start, end, city);
    }

    @Override
    public List<String> findCountry() {
        return accessMapper.findCountry();
    }

    @Override
    public List<String> findCity() {
        return accessMapper.findCity();
    }
}

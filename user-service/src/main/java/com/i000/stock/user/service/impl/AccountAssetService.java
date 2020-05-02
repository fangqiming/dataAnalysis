package com.i000.stock.user.service.impl;

import com.i000.stock.user.dao.bo.AccountAssetBO;
import com.i000.stock.user.dao.mapper.AccountAssetMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class AccountAssetService {

    @Resource
    private AccountAssetMapper accountAssetMapper;


    /**
     * 获取当天以及前一天的账户信息
     *
     * @param country
     * @return
     */
    public List<AccountAssetBO> findCurrentAndBefore(String country) {
        LocalDate currentDate = accountAssetMapper.getCurrent(country);
        List<AccountAssetBO> currentAsset = accountAssetMapper.find(country, currentDate);
        LocalDate before = getBeforeDate(country, currentDate);
        List<AccountAssetBO> beforeAsset = accountAssetMapper.find(country, before);
        //将两天的账户信息塞到一起
        currentAsset.addAll(beforeAsset);
        return currentAsset;
    }

    /**
     * 获取某一个时间段内的全部账户信息
     *
     * @param start 第一个账户的日期为 >= start 的账户
     * @param end   最后一个账户的日期 <= end 的账户
     * @return
     */
    public List<AccountAssetBO> findBetween(LocalDate start, LocalDate end, String country) {
        return accountAssetMapper.findBetween(start, end, country);
    }

    /**
     * 获取指定国家账户最新的日期
     *
     * @param country
     * @return
     */
    public LocalDate getCurrent(String country) {
        return accountAssetMapper.getCurrent(country);
    }

    public LocalDate getBeforeDate(String country, LocalDate date) {
        LocalDate result = accountAssetMapper.getL(country, date);
        if (Objects.isNull(result)) {
            //转化为账户的初始日期
            result = accountAssetMapper.getInit(country);
        }
        return result;
    }
}

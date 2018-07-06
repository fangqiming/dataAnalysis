package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.vo.AssetVo;
import com.i000.stock.user.api.entity.vo.PageTradeRecordVo;
import com.i000.stock.user.api.entity.vo.TradeRecordVo;
import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.TradeRecordService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.mapper.TradeRecordMapper;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.TradeRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:54 2018/5/2
 * @Modified By:
 */
@Component
@Transactional
public class TradeRecordServiceImpl implements TradeRecordService {

    @Resource
    private AssetService assetService;

    @Resource
    private TradeRecordMapper tradeRecordMapper;

    @Override
    public void save(TradeRecord tradeRecord) {
        tradeRecordMapper.insert(tradeRecord);
    }

    @Override
    public List<TradeRecord> find(LocalDate date, String userCode) {
        return tradeRecordMapper.find(date, userCode);
    }

    @Override
    public LocalDate getMaxDate(String userCode) {
        return tradeRecordMapper.getMaxDate(userCode);
    }

    @Override
    public Page<TradeRecord> search(String userCode, BaseSearchVo baseSearchVo) {
        baseSearchVo.setStart();
        List<LocalDate> localDates = tradeRecordMapper.searchByDate(userCode, baseSearchVo);
        Long total = tradeRecordMapper.pageTotal();
        List<TradeRecord> tradeRecord = tradeRecordMapper.findTradeRecord(userCode, localDates);
        Page<TradeRecord> result = new Page<>();
        result.setTotal(total);
        result.setList(tradeRecord);
        return result;
    }

    @Override
    public Page<PageTradeRecordVo> searchTradeAsset(String userCode, BaseSearchVo baseSearchVo) {
        Page<PageTradeRecordVo> result = new Page<>();
        Page<TradeRecord> pageInfo = search(userCode, baseSearchVo);
        if (!CollectionUtils.isEmpty(pageInfo.getList())) {
            Map<LocalDate, List<TradeRecord>> dateMapTrade = pageInfo.getList().stream().collect(groupingBy(TradeRecord::getOldDate));
            //降序排列才对
            List<Asset> assetInfo = assetService.findByDateUser(userCode, dateMapTrade.keySet()).stream().sorted((a, b) -> b.getDate().compareTo(a.getDate())).collect(Collectors.toList());
            List<PageTradeRecordVo> listResult = new ArrayList<>(assetInfo.size() * 4);
            for (Asset asset : assetInfo) {
                PageTradeRecordVo tmp = PageTradeRecordVo.builder()
                        .asset(ConvertUtils.beanConvert(asset, new AssetVo()))
                        .trade(ConvertUtils.listConvert(updatePrice(dateMapTrade.get(asset.getDate())), TradeRecordVo.class)).build();
                listResult.add(tmp);
            }
            result.setList(listResult);
        }
        result.setTotal(pageInfo.getTotal());
        return result;
    }


    private List<TradeRecord> updatePrice(List<TradeRecord> tradeRecords) {
        for (TradeRecord tradeRecord : tradeRecords) {
            //买入 没有卖出价格，，，卖出有买入价格  old是买入价 new是卖出价
            if ("BUY".equals(tradeRecord.getAction())) {
                tradeRecord.setNewDate(null);
                tradeRecord.setNewPrice(null);
            }
        }
        return tradeRecords;
    }
}

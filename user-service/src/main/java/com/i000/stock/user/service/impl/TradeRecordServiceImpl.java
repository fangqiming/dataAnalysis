package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.vo.TradeRecordVo;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.api.service.buiness.TradeRecordService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.mapper.TradeRecordMapper;
import com.i000.stock.user.dao.model.TradeRecord;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private CompanyService companyService;

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
    public Page<TradeRecordVo> search(String userCode, BaseSearchVo baseSearchVo) {
        baseSearchVo.setStart();
        List<TradeRecord> recode = tradeRecordMapper.search(userCode, baseSearchVo);
        Long total = tradeRecordMapper.pageTotal();
        Page<TradeRecordVo> result = new Page<>();
        List<TradeRecordVo> tradeRecordVos = setRecode(recode);
        result.setList(tradeRecordVos);
        result.setTotal(total);
        return result;
    }

    @Override
    public void updateAmountAndPriceById(Long id, BigDecimal newAmount, BigDecimal newPrice) {
        tradeRecordMapper.updateAmount(id, newPrice, newAmount);
    }

    @Override
    public TradeRecord getByNameAndDate(LocalDate date, String name, String userCode) {
        return tradeRecordMapper.getByNameAndDate(name, date, userCode);
    }

    @Override
    public Integer getAvgHoldDay(String userCode) {
        return tradeRecordMapper.getAvgHoldDay(userCode);
    }

    private List<TradeRecordVo> setRecode(List<TradeRecord> recode) {
        List<TradeRecordVo> result = new ArrayList<>();
        for (TradeRecord tradeRecord : recode) {
            TradeRecordVo tmp = ConvertUtils.beanConvert(tradeRecord, new TradeRecordVo());
            if ("BUY".equals(tradeRecord.getAction())) {
                tmp.setNewDate(null);
                tmp.setNewPrice(null);
                tmp.setTradeDate(tradeRecord.getOldDate());
            } else {
                tmp.setTradeDate(tradeRecord.getNewDate());
                BigDecimal gainRate = (tradeRecord.getNewPrice().subtract(tradeRecord.getOldPrice()))
                        .divide(tradeRecord.getOldPrice(), 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal(100));
                tmp.setGainRate(gainRate);
                tmp.setGain((tmp.getNewPrice().subtract(tmp.getOldPrice())).multiply(tmp.getAmount()));
            }
            tmp.setCompanyName(companyService.getNameByCode(tradeRecord.getName()));
            result.add(tmp);
        }
        return result;
    }

}

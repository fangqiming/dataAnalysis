package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.vo.OperatorVo;
import com.i000.stock.user.api.service.buiness.AssetService;
import com.i000.stock.user.api.service.buiness.HoldNowService;
import com.i000.stock.user.api.service.buiness.OperateSummaryService;
import com.i000.stock.user.api.service.buiness.TradeRecordService;
import com.i000.stock.user.dao.mapper.OperateSummaryMapper;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.HoldNow;
import com.i000.stock.user.dao.model.OperateSummary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:24 2018/7/4
 * @Modified By:
 */
@Service
public class OperateSummaryServiceImpl implements OperateSummaryService {

    @Resource
    private AssetService assetService;

    @Resource
    private HoldNowService holdNowService;

    @Resource
    private OperateSummaryMapper operateSummaryMapper;

    @Resource
    private TradeRecordService tradeRecordService;

    @Override
    public void updateSell(Integer holdDay, Integer profit, Integer loss, String userCode) {
        //首先要判断这个userCode是否存在 如果存在 才能够进行更新，否则需要进行插入
        checkUserCode(userCode);
        operateSummaryMapper.updateSell(holdDay, profit, loss, userCode);
    }

    @Override
    public void updateBuy(String userCode) {
        checkUserCode(userCode);
        operateSummaryMapper.updateBuy(userCode);
    }


    private void checkUserCode(String userCode) {
        Integer userCodeNumber = operateSummaryMapper.getUserCodeNumber(userCode);
        if (userCodeNumber == 0) {
            //如果不存在就初始化一个
            OperateSummary operateSummary = OperateSummary.builder().sellNumber(0).buyNumber(0)
                    .profitNumber(0).lossNumber(0).holdTotalDay(0).userCode(userCode).build();
            operateSummaryMapper.insert(operateSummary);
        }
    }

    @Override
    public OperatorVo getOperatorSummary(String userCode) {
        OperateSummary operateSummary = operateSummaryMapper.getByUserCode(userCode);
        if (Objects.isNull(operateSummary)) {
            return OperatorVo.builder().buyNumber(0)
                    .sellNumber(0)
                    .profitNumber(0)
                    .lossNumber(0)
                    .avgHoldDay(0)
                    .winRate(BigDecimal.ZERO)
                    .holdNumber(0)
                    .minGain(BigDecimal.ZERO)
                    .maxGain(BigDecimal.ZERO)
                    .avgProfitRate(BigDecimal.ZERO).build();
        }
        Asset asset = assetService.getLately(userCode);
        List<HoldNow> holdNows = holdNowService.find(userCode);
        for (HoldNow holdNow : holdNows) {
            //持有的总天数

            //赚钱股和亏钱股数
            if (holdNow.getNewPrice().compareTo(holdNow.getOldPrice()) >= 0) {
                operateSummary.setProfitNumber(operateSummary.getProfitNumber() + 1);
            } else {
                operateSummary.setLossNumber(operateSummary.getLossNumber() + 1);
            }
        }

        Integer avgHoldNumber = tradeRecordService.getAvgHoldDay(userCode);
        BigDecimal winRate = BigDecimal.ZERO;
        //赚钱的股票+亏钱的股票就是一共持有过的股票
        Integer holdNumber = operateSummary.getProfitNumber() + operateSummary.getLossNumber();
        BigDecimal avgProfitRate = BigDecimal.ZERO;
        if (holdNumber != 0) {
            avgHoldNumber = Math.toIntExact(Math.round(operateSummary.getHoldTotalDay() / 1.0 / holdNumber));
            winRate = new BigDecimal(operateSummary.getProfitNumber()).divide(new BigDecimal(holdNumber), 4, BigDecimal.ROUND_HALF_UP);
            avgProfitRate = asset.getTotalGain().divide(new BigDecimal(holdNumber), 4, BigDecimal.ROUND_HALF_UP);
        }

        return OperatorVo.builder().buyNumber(operateSummary.getBuyNumber())
                .sellNumber(operateSummary.getSellNumber())
                .maxGain(assetService.getMaxGain(userCode))
                .minGain(assetService.getMinGain(userCode))
                .profitNumber(operateSummary.getProfitNumber())
                .lossNumber(operateSummary.getLossNumber())
                .avgHoldDay(avgHoldNumber)
                .winRate(winRate.multiply(new BigDecimal(100)))
                .holdNumber(holdNows.size())
                //总收益率除以操作过的所有股票
                .avgProfitRate(avgProfitRate.multiply(new BigDecimal(100))).build();
    }
}

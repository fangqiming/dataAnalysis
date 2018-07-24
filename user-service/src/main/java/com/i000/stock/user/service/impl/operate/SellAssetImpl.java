package com.i000.stock.user.service.impl.operate;

import com.i000.stock.user.api.service.buiness.AssetUpdateService;
import com.i000.stock.user.api.service.buiness.HoldNowService;
import com.i000.stock.user.api.service.buiness.OperateSummaryService;
import com.i000.stock.user.api.service.buiness.TradeRecordService;
import com.i000.stock.user.api.service.original.TradeService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description: 卖出股票  那么肯定需要数据库中的该条记录得到股票的份数  ==>获取股票的份数  获取卖出的价格  增加 余额  删掉股票
 * @Date:Created in 16:58 2018/4/26
 * @Modified By:
 */
@Component
public class SellAssetImpl implements AssetUpdateService {

    @Resource
    private OperateSummaryService operateSummaryService;

    @Resource
    private HoldNowService holdNowService;

    @Resource
    private TradeService tradeService;

    @Resource
    private TradeRecordService tradeRecordService;

    @Override
    public Asset update(Asset asset, Hold trade) {
        if (Objects.isNull(asset)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "用户账户没有初始化");
        }
        BigDecimal price = tradeService.getSellPrice(trade.getName());
        if (Objects.isNull(price)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "获取不到卖出股票的价格，数据可能存在问题");
        }
        HoldNow holdNow = holdNowService.getByNameDateType(asset.getUserCode(), trade.getName(),
                trade.getOldDate(), trade.getType());
        asset.setBalance(asset.getBalance().add(price.multiply(new BigDecimal(holdNow.getAmount()))));
        record(asset, holdNow);
        holdNowService.deleteById(holdNow.getId());
        return asset;
    }

    private void record(Asset asset, HoldNow trade) {
        if (trade.getAmount() > 0) {
            TradeRecord build = TradeRecord.builder().type(trade.getType()).name(trade.getName())
                    .action("SELL").oldDate(trade.getOldDate()).oldPrice(trade.getOldPrice())
                    .newDate(asset.getDate()).newPrice(trade.getNewPrice()).amount(new BigDecimal(trade.getAmount()))
                    .userCode(asset.getUserCode()).build();
            //todo 此处有待优化，如果不是同一年就会出现问题
            int holdDay = asset.getDate().getDayOfYear() - trade.getOldDate().getDayOfYear();

            if (trade.getNewPrice().compareTo(trade.getOldPrice()) >= 0) {
                //获利
                operateSummaryService.updateSell(holdDay, 1, 0, asset.getUserCode());
            } else {
                operateSummaryService.updateSell(holdDay, 0, 1, asset.getUserCode());
            }
            tradeRecordService.save(build);
        }
    }
}
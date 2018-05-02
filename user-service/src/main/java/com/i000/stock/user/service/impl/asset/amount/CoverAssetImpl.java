package com.i000.stock.user.service.impl.asset.amount;

import com.i000.stock.user.api.service.AssetUpdateService;
import com.i000.stock.user.api.service.HoldNowService;
import com.i000.stock.user.api.service.TradeRecordService;
import com.i000.stock.user.api.service.TradeService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description: 平仓 做空账户减少  删除相应的做空股票
 * @Date:Created in 16:59 2018/4/26
 * @Modified By:
 */
@Component
public class CoverAssetImpl implements AssetUpdateService {

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
        BigDecimal price = tradeService.getCoverPrice(trade.getName());
        if (Objects.isNull(price)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "获取不到平仓股票的价格，数据可能存在问题");
        }
        HoldNow holdNow = holdNowService.getByNameDateType(asset.getUserCode(), trade.getName(),
                trade.getOldDate(), trade.getType());

        asset.setCover(asset.getCover().add(price.multiply(new BigDecimal(holdNow.getAmount()))));
        holdNowService.deleteById(holdNow.getId());
        record(asset, holdNow);
        return asset;
    }

    private void record(Asset asset,HoldNow trade) {
        if (trade.getAmount() > 0) {
            TradeRecord build = TradeRecord.builder().type(trade.getType()).name(trade.getName())
                    .action(trade.getAction()).oldDate(trade.getOldDate()).oldPrice(trade.getOldPrice())
                    .newDate(asset.getDate()).newPrice(trade.getNewPrice()).amount(new BigDecimal(trade.getAmount()))
                    .userCode(asset.getUserCode()).build();
            tradeRecordService.save(build);
        }
    }
}

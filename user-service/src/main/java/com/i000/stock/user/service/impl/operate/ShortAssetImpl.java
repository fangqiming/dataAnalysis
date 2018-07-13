package com.i000.stock.user.service.impl.operate;

import com.i000.stock.user.api.service.buiness.AssetUpdateService;
import com.i000.stock.user.api.service.buiness.HoldNowService;
import com.i000.stock.user.api.service.buiness.TradeRecordService;
import com.i000.stock.user.api.service.buiness.UserInfoService;
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
 * @Description: 做空股票   做空账户增加  股票记录增加
 * @Date:Created in 16:59 2018/4/26
 * @Modified By:
 */
@Component
public class ShortAssetImpl implements AssetUpdateService {

    @Resource
    private HoldNowService holdNowService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TradeRecordService tradeRecordService;

    @Override
    public Asset update(Asset asset, Hold trade) {
        if (Objects.isNull(asset)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "用户账户没有初始化");
        }
        trade.setAmount(0);
        BigDecimal oneHandMoney = trade.getOldPrice().multiply(new BigDecimal(100));
        UserInfo userInfo = userInfoService.getByName(asset.getUserCode());
        BigDecimal oneShareMoney = userInfo.getInitAmount().divide(userInfo.getInitNum(), 4, RoundingMode.HALF_UP);
        BigDecimal canBuyHandNum = oneShareMoney.divide(oneHandMoney, 0, BigDecimal.ROUND_HALF_UP);
        //设置做空账户追加
        asset.setCover(asset.getCover().subtract(oneHandMoney.multiply(canBuyHandNum)));
        //设置做空的份数
        trade.setAmount(canBuyHandNum.multiply(new BigDecimal(100)).intValue());

        HoldNow holdNow = ConvertUtils.beanConvert(trade, new HoldNow());
        holdNow.setId(null);
        //将新买的股票记录到数据库中
        holdNow.setUserCode(asset.getUserCode());
        holdNowService.save(holdNow);
        record(asset, holdNow);
        return asset;
    }

    private void record(Asset asset, HoldNow trade) {
        if (trade.getAmount() > 0) {
            TradeRecord build = TradeRecord.builder().type(trade.getType()).name(trade.getName())
                    .action("SHORT").oldDate(trade.getOldDate()).oldPrice(trade.getOldPrice())
                    .newDate(asset.getDate()).newPrice(trade.getNewPrice()).amount(new BigDecimal(trade.getAmount()))
                    .userCode(asset.getUserCode()).build();
            tradeRecordService.save(build);
        }
    }
}

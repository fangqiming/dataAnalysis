package com.i000.stock.user.service.impl.asset.amount;

import com.i000.stock.user.api.service.*;
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
 * @Description: 此处为买股票  账户余额减少  用户持股追加
 * @Date:Created in 16:58 2018/4/26
 * @Modified By:
 */
@Component
public class BuyAssetImpl implements AssetUpdateService {

    @Resource
    private OperateSummaryService operateSummaryService;

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
        BigDecimal balance = asset.getBalance();

        //此处计算的是买入的份数
        for (int i = 1; i <= canBuyHandNum.intValue(); i++) {
            balance = balance.subtract(oneHandMoney);
            if (balance.compareTo(BigDecimal.ZERO) >= 0) {
                //余额够 股票份数就追加1份
                trade.setAmount(trade.getAmount() + 100);
                //余额减少一份的钱数
                asset.setBalance(asset.getBalance().subtract(oneHandMoney));
            }
        }
        HoldNow holdNow = ConvertUtils.beanConvert(trade, new HoldNow());
        holdNow.setId(null);
        //将新买的股票记录到数据库中
        holdNow.setUserCode(asset.getUserCode());

        record(asset, holdNow);
        holdNowService.save(holdNow);

        //更新买操作
        operateSummaryService.updateBuy(holdNow.getUserCode());
        return asset;
    }


    private void record(Asset asset, HoldNow trade) {
        if (trade.getAmount() > 0) {
            TradeRecord build = TradeRecord.builder().type(trade.getType()).name(trade.getName())
                    .action("BUY").oldDate(trade.getOldDate()).oldPrice(trade.getOldPrice())
                    .newDate(asset.getDate()).newPrice(trade.getNewPrice()).amount(new BigDecimal(trade.getAmount()))
                    .userCode(asset.getUserCode()).build();
            tradeRecordService.save(build);
        }
    }
}

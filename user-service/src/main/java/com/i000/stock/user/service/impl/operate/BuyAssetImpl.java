package com.i000.stock.user.service.impl.operate;

import com.i000.stock.user.api.service.buiness.*;
import com.i000.stock.user.api.service.original.HoldService;
import com.i000.stock.user.api.service.original.TradeService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.model.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
    private AssetService assetService;

    @Resource
    private OperateSummaryService operateSummaryService;

    @Resource
    private HoldNowService holdNowService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TradeRecordService tradeRecordService;

    //应该是从hold中查询
    @Resource
    private HoldService holdService;

    @Resource
    private TradeService tradeService;


    @Override
    public Asset update(Asset asset, Hold trade) {

        //最大的问题在于卖出后，股票的价值已经发生了变化，因而不能这样处理。
        //办法 1.根据当前的持股数量 与 账户余额 初始资金来修正每支股票买入的数量

        if (Objects.isNull(asset)) {
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(), "用户账户没有初始化");
        }
        trade.setAmount(0);
        BigDecimal oneHandMoney = trade.getOldPrice().multiply(new BigDecimal(100));

        BigDecimal oneShareMoney = getOneShareMoney(asset.getUserCode(), false);
        BigDecimal canBuyHandNum = oneShareMoney.divide(oneHandMoney, 0, BigDecimal.ROUND_HALF_UP);
        //设置交易数量
        trade.setAmount(canBuyHandNum.multiply(new BigDecimal(100)).intValue());
        //设置余额
        asset.setBalance(asset.getBalance().subtract(canBuyHandNum.multiply(oneHandMoney)));

        HoldNow holdNow = ConvertUtils.beanConvert(trade, new HoldNow());
        holdNow.setId(null);
        //将新买的股票记录到数据库中
        holdNow.setUserCode(asset.getUserCode());

        //保存了交易记录
        record(asset, holdNow);
        //保存当前持股信息
        holdNowService.save(holdNow);

        //更新购买操作
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

    public Integer getHoldNum(boolean isRecommend) {
        LocalDate date = holdService.getMaxHold();
        //当前持股数量
        Integer holdCount = holdService.getHoldCount(date);
        if (isRecommend) {
            return holdCount;
        }
        Integer sellNum = tradeService.getSellNum(date);
        Integer buyNum = tradeService.getBuyNum(date);
        return holdCount - buyNum + sellNum;
    }


    public BigDecimal getOneShareMoney(String userCode, boolean isRecommend) {
        //不管是否融资,直接为总资产/12
        UserInfo userInfo = userInfoService.getByName(userCode);
        Asset lately = assetService.getLately(userInfo.getName());
        return (lately.getBalance().add(lately.getStock())).divide(userInfo.getInitNum(), 0, BigDecimal.ROUND_DOWN);


//        Integer holdNum = getHoldNum(isRecommend);
//        BigDecimal oneShareMoney;
//        UserInfo userInfo = userInfoService.getByName(userCode);
//        Asset lately = assetService.getLately(userInfo.getName());
//        //余额为正 并且 允许的份数也大于持股份数
//        if (lately.getBalance().compareTo(BigDecimal.ZERO) > 0 && userInfo.getInitNum().compareTo(new BigDecimal(holdNum)) > 0) {
//            oneShareMoney = (lately.getBalance())
//                    .divide(userInfo.getInitNum().subtract(new BigDecimal(holdNum)), 0, BigDecimal.ROUND_HALF_UP);
//        } else if (lately.getBalance().compareTo(BigDecimal.ZERO) > 0) {
//            //关键在于此处的融资金额究竟应该是多少。。。
//            BigDecimal all = lately.getStock().add(lately.getBalance()).add(lately.getCover());
//
//            //总的资金除以总的持股数量
//            oneShareMoney = all.divide(new BigDecimal(holdNum), 0, BigDecimal.ROUND_HALF_UP);
//        } else {
//            BigDecimal all = lately.getStock().add(lately.getCover());
//
//            //总的资金除以总的持股数量
//            oneShareMoney = all.divide(new BigDecimal(holdNum), 0, BigDecimal.ROUND_HALF_UP);
//
//        }
//        return oneShareMoney;
    }

}

package com.i000.stock.user.service.impl.asset.money;

import com.i000.stock.user.api.entity.bo.AssetInitBo;
import com.i000.stock.user.api.service.AssetUpdateService;
import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.Trade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description: 买股票  账户余额减少  此实现为每次买入同样的钱数
 * @Date:Created in 16:58 2018/4/26
 * @Modified By:
 */
@Component
public class BuyAssetSomeImpl implements AssetUpdateService {

    @Autowired
    private AssetInitBo assetInitBo;

    @Resource
    private HoldService holdService;

    @Override
    public Asset update(Asset asset, Trade trade) {
        BigDecimal amount=getAmount(trade);
        //更新资产表  此种情况下余额信息必须要有，否则抛出异常
        if (Objects.nonNull(asset) && Objects.nonNull(asset.getBalance())) {
            asset.setBalance(asset.getBalance().subtract(trade.getPrice().multiply(amount).multiply(new BigDecimal(100))));
        }else{
            throw new ServiceException(ApplicationErrorMessage.NOT_EXISTS.getCode(),"虚拟账户余额没有设置");
        }
        asset.setDate(trade.getDate());
        return asset;
    }

    /**
     * 根据交易信息更新股票的份数并返回买入的股票份数
     * @param trade
     * @return
     */
    private BigDecimal getAmount(Trade trade){
        //此时的当前持股已经存在了今日的买入股数
        BigDecimal money=assetInitBo.getAmount().divide(assetInitBo.getQuantity(),4,BigDecimal.ROUND_UP);
        BigDecimal multiply = trade.getPrice().multiply(new BigDecimal(100));
        //四舍五入后得到的买入手数
        BigDecimal divide= money.divide(multiply, 0, BigDecimal.ROUND_UP);
        holdService.updateAmount(trade.getDate(), trade.getName(), divide.multiply(new BigDecimal(100)));
        return divide;
    }

}

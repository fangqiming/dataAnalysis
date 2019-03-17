package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.i000.stock.user.api.entity.vo.OperatorUsVO;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.TradeRecordUsMapper;
import com.i000.stock.user.dao.model.TradeRecordUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class TradeRecordUsService {

    @Autowired
    private TradeRecordUsMapper tradeRecordUsMapper;

    /**
     * 获取指定用户的交易统计信息
     * 不应该放在dao层，需要移到Servcie层
     *
     * @param user
     * @return
     */
    public OperatorUsVO getOperatorInfo(String user) {

        Integer tradeNumber = getCountByActionAndUser(null, user);
        Integer buyNumber = getCountByActionAndUser("BUY", user);
        Integer sellNumber = getCountByActionAndUser("SELL", user);
        Integer shortNumber = getCountByActionAndUser("SHORT", user);
        Integer coverNumber = getCountByActionAndUser("COVER", user);

        //当前持股数
        BigDecimal shortAndSellNumber = tradeRecordUsMapper.getCoverAndSellNumber(user);
        //交易总量 - 平仓和卖出的数量
        Integer holdCount = buyNumber + shortNumber - sellNumber - coverNumber;
        BigDecimal earnMoneyNumber = tradeRecordUsMapper.getEarnMoneyNumber(user);

        BigDecimal lossMoneyNumber = shortAndSellNumber.subtract(earnMoneyNumber);
        BigDecimal winRate = BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(shortAndSellNumber) != 0) {
            winRate = earnMoneyNumber.divide(shortAndSellNumber, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
        }
        BigDecimal avgHoldDay = tradeRecordUsMapper.getAvgHoldDay(user);
        avgHoldDay = Objects.isNull(avgHoldDay) ? BigDecimal.ZERO : avgHoldDay;
        return OperatorUsVO.builder().tradeNumber(tradeNumber).buyNumber(buyNumber)
                .sellNumber(sellNumber).shortNumber(shortNumber).coverNumber(coverNumber)
                .avgHoldDay(avgHoldDay.setScale(1, BigDecimal.ROUND_UP))
                .lossNumber(lossMoneyNumber.intValue()).profitNumber(earnMoneyNumber.intValue())
                .holdNumber(holdCount).winRate(winRate).build();
    }

    /**
     * 只算指定用户的做空胜率
     *
     * @param user
     * @return
     */
    public BigDecimal getShortWinRate(String user) {
        //获取指定用户的全部平仓数量
        Integer coverNumber = getCountByActionAndUser("COVER", user);
        //获取平仓为赚钱交易的数量
        BigDecimal shortEarnMoneyNumber = tradeRecordUsMapper.getShortEarnMoneyNumber(user);
        if (coverNumber <= 0) {
            return BigDecimal.ZERO;
        }
        return shortEarnMoneyNumber.divide(new BigDecimal(coverNumber), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
    }

    public void insert(TradeRecordUs tradeRecordUs) {
        tradeRecordUsMapper.insert(tradeRecordUs);
    }

    public PageResult<TradeRecordUs> search(BaseSearchVo baseSearchVo, String user) {
        List<String> sellOrCover = Arrays.asList("SELL", "COVER");
        EntityWrapper<TradeRecordUs> ew = new EntityWrapper();
        ew.where("user = {0}", user).and().in("action", sellOrCover)
                .orderBy("new_date", false);
        Page page = new Page(baseSearchVo.getPageNo(), baseSearchVo.getPageSize());
        List<TradeRecordUs> tradeRecordUses = tradeRecordUsMapper.selectPage(page, ew);

        BigDecimal shortAndSellNumber = tradeRecordUsMapper.getCoverAndSellNumber(user);
        PageResult<TradeRecordUs> result = new PageResult<>();
        result.setList(tradeRecordUses);
        result.setTotal(shortAndSellNumber.longValue());
        return result;
    }

    private Integer getCountByActionAndUser(String action, String user) {
        EntityWrapper<TradeRecordUs> ew = new EntityWrapper<>();
        ew.where("user = {0}", user);
        if (!StringUtils.isEmpty(action)) {
            ew.and("action = {0}", action);
        }
        return tradeRecordUsMapper.selectCount(ew);
    }
}

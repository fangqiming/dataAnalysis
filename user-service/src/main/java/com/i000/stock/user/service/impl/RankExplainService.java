package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.i000.stock.user.api.entity.vo.RankVo;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.RankExplainMapper;
import com.i000.stock.user.dao.mapper.RankMapper;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.external.StockChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


@Service
public class RankExplainService {

    @Autowired
    private RankExplainMapper rankExplainMapper;

    @Autowired
    private RankMapper rankMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FinancialNowService financialNowService;

    @Autowired
    private FundamentalsService fundamentalsService;

    @Autowired
    private StockChangeService stockChangeService;

    @Autowired
    private StockPriceService stockPriceService;


    public BigDecimal getBeatRateByScore(BigDecimal score) {
        BigDecimal gtCount = rankMapper.getGtCount(score);
        BigDecimal count = rankMapper.getCount();
        if (Objects.nonNull(count)) {
            return gtCount.divide(count, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
        }
        return null;
    }

    public StockRank getRankByCode(String code) {
        EntityWrapper<StockRank> ew = new EntityWrapper<>();
        ew.where("code = {0}", code);
        return getRankByEw(ew);
    }

    public RankExplain getRankExplainByScore(BigDecimal score) {
        EntityWrapper<RankExplain> ew = new EntityWrapper<>();
        ew.where("score >= {0}", score).orderBy("score").last("limit 1");
        return getRankExplainByEw(ew);
    }

    public PageResult<RankVo> searchRankVo(BaseSearchVo baseSearchVo, String filed, Boolean isAsc) {
        PageResult<RankVo> result = new PageResult<>();
        EntityWrapper<StockRank> ew = new EntityWrapper();
        ew.orderBy(filed, isAsc);
        Page page = new Page(baseSearchVo.getPageNo(), baseSearchVo.getPageSize());
        List<StockRank> stockRanks = rankMapper.selectPage(page, ew);
        List<RankVo> rankVos = new ArrayList<>(stockRanks.size());
        for (StockRank stockRank : stockRanks) {
            RankVo rankVo = new RankVo();
            RankVo rankVo1 = ConvertUtils.beanConvert(stockRank, rankVo);
            rankVo1.setAiScore(new BigDecimal(100).subtract(stockRank.getScore()));
            String url = String.format("https://xueqiu.com/S/%s", stockRank.getCode().startsWith("6")
                    ? "SH" + stockRank.getCode() : "SZ" + stockRank.getCode());

            String changeStockUrl = String.format("https://xueqiu.com/snowman/S/%s/detail#/INSIDER",
                    stockRank.getCode().startsWith("6") ? "SH" + stockRank.getCode() : "SZ" + stockRank.getCode());
            rankVo1.setUrl(url);
            rankVo1.setChangeStock(stockChangeService.getChangeNumber(stockRank.getCode()));
            rankVo1.setChangeStockUrl(changeStockUrl);
            Fundamentals fun = fundamentalsService.getByCode(stockRank.getCode());
            if (Objects.nonNull(fun)) {
                rankVo1.setAvgPe(fun.getAvgPe());
                rankVo1.setPe(fun.getPe());
                rankVo1.setSharpeRatio(fun.getSharpeRatio());
                rankVo1.setPeg(fun.getPeg());
                rankVo1.setName(fun.getName());
            }
            //添加增减持信息
            addStockChangeInfo(rankVo1);
            rankVos.add(rankVo1);
        }
        BigDecimal count = rankMapper.getCount();
        result.setList(rankVos);
        result.setTotal(count.longValue());
        return result;
    }

    /**
     * 追加股票的增减持信息
     *
     * @param rankVo
     * @return
     */
    public void addStockChangeInfo(RankVo rankVo) {
        List<StockChange> nearYear = stockChangeService.findNearYear(rankVo.getCode());
        if (!CollectionUtils.isEmpty(nearYear)) {
            StockPrice stockPrice = stockPriceService.getByCode(rankVo.getCode());
            //总金额
            double amount = nearYear.stream().map(a -> a.getChangeNumber().multiply(a.getTradePrice())).mapToDouble(a -> a.doubleValue()).sum();
            //总股份数
            int share = nearYear.stream().mapToInt(a -> a.getChangeNumber().intValue()).sum();
            BigDecimal avgCost;
            if (share != 0) {
                avgCost = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(share), 2, BigDecimal.ROUND_UP);
            } else {
                avgCost = BigDecimal.valueOf(amount);
            }
            rankVo.setAvgCost(avgCost);
            BigDecimal totalAmount = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(10000), 1, BigDecimal.ROUND_UP);
            rankVo.setAmount(totalAmount);
            if (Objects.nonNull(stockPrice)) {
                rankVo.setClose(stockPrice.getClose());
            }
        }
    }


    private RankExplain getRankExplainByEw(EntityWrapper<RankExplain> ew) {
        List<RankExplain> rankExplains = rankExplainMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(rankExplains)) {
            return rankExplains.get(0);
        }
        return null;
    }

    private StockRank getRankByEw(EntityWrapper<StockRank> ew) {
        List<StockRank> stockRankExplains = rankMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(stockRankExplains)) {
            return stockRankExplains.get(0);
        }
        return null;
    }


}

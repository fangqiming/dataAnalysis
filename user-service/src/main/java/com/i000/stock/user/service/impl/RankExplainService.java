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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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


    public BigDecimal getBeatRateByScore(BigDecimal score) {
        BigDecimal gtCount = rankMapper.getGtCount(score);
        BigDecimal count = rankMapper.getCount();
        if (Objects.nonNull(count)) {
            return gtCount.divide(count, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
        }
        return null;
    }

    public Rank getRankByCode(String code) {
        EntityWrapper<Rank> ew = new EntityWrapper<>();
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
        EntityWrapper<Rank> ew = new EntityWrapper();
        ew.orderBy(filed, isAsc);
        Page page = new Page(baseSearchVo.getPageNo(), baseSearchVo.getPageSize());
        List<Rank> ranks = rankMapper.selectPage(page, ew);
        List<RankVo> rankVos = new ArrayList<>(ranks.size());
        for (Rank rank : ranks) {
            RankVo rankVo = new RankVo();
            RankVo rankVo1 = ConvertUtils.beanConvert(rank, rankVo);
            rankVo1.setAiScore(new BigDecimal(100).subtract(rank.getScore()));
            String url = String.format("https://xueqiu.com/S/%s", rank.getCode().startsWith("6")
                    ? "SH" + rank.getCode() : "SZ" + rank.getCode());

            String changeStockUrl = String.format("https://xueqiu.com/snowman/S/%s/detail#/INSIDER",
                    rank.getCode().startsWith("6") ? "SH" + rank.getCode() : "SZ" + rank.getCode());
            rankVo1.setUrl(url);
            rankVo1.setChangeStock(stockChangeService.getChangeNumber(rank.getCode()));
            rankVo1.setChangeStockUrl(changeStockUrl);
            Fundamentals fun = fundamentalsService.getByCode(rank.getCode());
            if (Objects.nonNull(fun)) {
                rankVo1.setAvgPe(fun.getAvgPe());
                rankVo1.setPe(fun.getPe());
                rankVo1.setSharpeRatio(fun.getSharpeRatio());
                rankVo1.setPeg(fun.getPeg());
                rankVo1.setName(fun.getName());
            }
            rankVos.add(rankVo1);
        }
        BigDecimal count = rankMapper.getCount();
        result.setList(rankVos);
        result.setTotal(count.longValue());
        return result;
    }


    private RankExplain getRankExplainByEw(EntityWrapper<RankExplain> ew) {
        List<RankExplain> rankExplains = rankExplainMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(rankExplains)) {
            return rankExplains.get(0);
        }
        return null;
    }

    private Rank getRankByEw(EntityWrapper<Rank> ew) {
        List<Rank> rankExplains = rankMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(rankExplains)) {
            return rankExplains.get(0);
        }
        return null;
    }


}

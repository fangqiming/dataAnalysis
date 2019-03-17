package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.i000.stock.user.api.entity.bo.RankResultBo;
import com.i000.stock.user.api.entity.vo.RankVo;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.DiagnosisFlushMapper;
import com.i000.stock.user.dao.mapper.RankExplainMapper;
import com.i000.stock.user.dao.mapper.RankMapper;
import com.i000.stock.user.dao.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RankExplainService {

    @Autowired
    private RankExplainMapper rankExplainMapper;

    @Autowired
    private RankMapper rankMapper;

    @Autowired
    private DiagnosisFlushMapper diagnosisFlushMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private FinancialNowService financialNowService;


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
        PageResult<DiagnosisFlush> rankPage = searchRank(baseSearchVo, filed, isAsc);
        List<RankVo> rankVos = new ArrayList<>();

        if (!CollectionUtils.isEmpty(rankPage.getList())) {
            List<DiagnosisFlush> ranks = rankPage.getList();
            List<String> codes = ranks.stream().map(a -> a.getCode()).collect(Collectors.toList());
            //追加名字。
            //还需要追加财务数据
            List<Company> companies = companyService.findByCodes(codes);
            List<FinancialNow> financials = financialNowService.findByCode(codes);
            Map<String, List<FinancialNow>> financialMap = financials.stream().collect(Collectors.groupingBy(FinancialNow::getCode));
            Map<String, List<Company>> companyMap = companies.stream().collect(Collectors.groupingBy(Company::getCode));
            rankVos = ConvertUtils.listConvert(ranks, RankVo.class, (d, s) -> {
                FinancialNow financialNow = financialMap.get(s.getCode()).get(0);
                d.setName(companyMap.get(s.getCode()).get(0).getName());
                d.setDebtRate(financialNow.getDebtRate());
                d.setGrossProfitMargin(financialNow.getGrossProfitMargin());
                d.setMarketCap(financialNow.getMarketCap());
                d.setPeRatio(financialNow.getPeRatio());
            });
        }

        PageResult<RankVo> result = new PageResult<>();
        result.setTotal(rankPage.getTotal());
        result.setList(rankVos);
        return result;
    }

    private PageResult<DiagnosisFlush> searchRank(BaseSearchVo baseSearchVo, String filed, Boolean isAsc) {
        EntityWrapper<DiagnosisFlush> ew = new EntityWrapper();
        //同花顺的得分与AI的得分是反的。
        ew.orderBy(filed, isAsc);
        Page page = new Page(baseSearchVo.getPageNo(), baseSearchVo.getPageSize());
        List<DiagnosisFlush> ranks = diagnosisFlushMapper.selectPage(page, ew);
        BigDecimal count = rankMapper.getCount();
        PageResult<DiagnosisFlush> result = new PageResult<>();
        result.setTotal(count.longValue());
        result.setList(ranks);
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

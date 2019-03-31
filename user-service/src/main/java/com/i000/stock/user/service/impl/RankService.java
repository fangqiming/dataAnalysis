package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.bo.KVBo;
import com.i000.stock.user.api.entity.bo.RankBO;
import com.i000.stock.user.api.entity.vo.IndustryRankVO;
import com.i000.stock.user.api.entity.vo.RankScatterVO;
import com.i000.stock.user.api.service.external.CompanyService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.mapper.RankMapper;
import com.i000.stock.user.dao.model.Company;
import com.i000.stock.user.dao.model.Rank;
import com.i000.stock.user.service.impl.us.PatternUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class RankService {

    @Autowired
    private RankMapper rankMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EverydayStockService everydayStockService;

    public List<Rank> finaAll() {
        return rankMapper.selectList(null);
    }

    public List<Rank> findByCode(List<String> code) {
        EntityWrapper<Rank> ew = new EntityWrapper<>();
        ew.in("code", code);
        return rankMapper.selectList(ew);
    }

    public Rank getEverydayStock() {
        EntityWrapper<Rank> ew = new EntityWrapper<>();
        ew.orderBy("score", true).last("limit 8");
        List<Rank> ranks = rankMapper.selectList(ew);
        List<String> inEveryStock = everydayStockService.findInEveryStock();
        for (int i = 2; i < ranks.size(); i++) {
            if (inEveryStock.contains(ranks.get(i).getCode())) {
                continue;
            }
            return ranks.get(i);
        }
        return null;
    }

    public void save(String content) throws IOException {
        List<Rank> ranks = parseToRank(content);
        if (!CollectionUtils.isEmpty(ranks)) {
            if (canSaveClearRank(ranks.get(0).getDate())) {
                for (Rank rank : ranks) {
                    rankMapper.insert(rank);
                }
            }
        }
    }

    private List<Rank> parseToRank(String content) throws IOException {
        if (!StringUtils.isEmpty(content)) {
            List<Rank> result = new ArrayList<>(3600);
            String[] items = content.split(PatternUtil.LINE.pattern());
            for (String item : items) {
                if (!StringUtils.isEmpty(item)) {
                    result.add(new Rank(item));
                }
            }
            return result;
        }
        return new ArrayList<>(0);
    }

    private boolean canSaveClearRank(LocalDate date) {
        EntityWrapper<Rank> ew = new EntityWrapper<>();
        ew.last("limit 1");
        List<Rank> ranks = rankMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(ranks)) {
            //当天的报告已经存在
            if (date.compareTo(ranks.get(0).getDate()) > 0) {
                rankMapper.truncate();
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public Rank getByCode(String code) {
        return rankMapper.getByCode(code);
    }

    public List<IndustryRankVO> findIndustry() {
        List<Rank> ranks = rankMapper.selectList(null);
        List<IndustryRankVO> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ranks)) {
            List<RankBO> rankBOS = ConvertUtils.listConvert(ranks, RankBO.class);
            List<String> codes = ranks.stream().map(a -> a.getCode()).collect(Collectors.toList());
            List<Company> companies = companyService.findByCodes(codes);
            Map<String, List<Company>> companyMap = companies.stream().collect(Collectors.groupingBy(Company::getCode));
            for (RankBO rankBO : rankBOS) {
                List<Company> temp = companyMap.get(rankBO.getCode());
                if (!CollectionUtils.isEmpty(temp)) {
                    rankBO.setIndustry(temp.get(0).getIndustry());
                }
            }
            Map<String, List<RankBO>> industry = rankBOS.stream().collect(Collectors.groupingBy(RankBO::getIndustry));
            for (Map.Entry<String, List<RankBO>> entry : industry.entrySet()) {
                if (!CollectionUtils.isEmpty(entry.getValue())) {
                    IndustryRankVO industryRankVO = new IndustryRankVO();
                    industryRankVO.setIndustryName(entry.getKey());
                    industryRankVO.setNumber(entry.getValue().size());
                    double score = entry.getValue().stream().mapToDouble(a -> a.getScore().doubleValue()).average().getAsDouble();
                    industryRankVO.setScore(new BigDecimal(score).setScale(2, BigDecimal.ROUND_UP));
                    result.add(industryRankVO);
                }
            }
        }
        result.sort(Comparator.comparing(IndustryRankVO::getScore));
        return result.size() > 20 ? result.subList(0, 20) : result;
    }

    public List<RankScatterVO> findRankScatter() {
        List<RankScatterVO> result = new ArrayList<>();
        BigDecimal total = rankMapper.getCount();
        BigDecimal rang0_1 = rankMapper.getScoreRangeCount(0, 1);
        BigDecimal rang1_3 = rankMapper.getScoreRangeCount(1, 3);
        BigDecimal rang3_10 = rankMapper.getScoreRangeCount(3, 10);
        BigDecimal rang10_50 = rankMapper.getScoreRangeCount(10, 50);

        result.add(RankScatterVO.builder().name("0-1分").score(rang0_1.setScale(2, BigDecimal.ROUND_UP))
                .rate(rang0_1.divide(total, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100))).build());
        result.add(RankScatterVO.builder().name("1-3分").score(rang1_3.setScale(2, BigDecimal.ROUND_UP))
                .rate(rang1_3.divide(total, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100))).build());
        result.add(RankScatterVO.builder().name("3-10分").score(rang3_10.setScale(2, BigDecimal.ROUND_UP))
                .rate(rang3_10.divide(total, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100))).build());
        result.add(RankScatterVO.builder().name("10-50分").score(rang10_50.setScale(2, BigDecimal.ROUND_UP))
                .rate(rang10_50.divide(total, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100))).build());
        return result;
    }

}

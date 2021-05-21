package com.i000.stock.user.service.impl.us.service;

import com.i000.stock.user.api.entity.bo.IndexUsBo;
import com.i000.stock.user.api.entity.bo.RelativeProfitBO;
import com.i000.stock.user.api.entity.vo.GainVo;
import com.i000.stock.user.api.entity.vo.HistoryProfitVO;
import com.i000.stock.user.api.entity.vo.PageGainVo;
import com.i000.stock.user.api.entity.vo.UsYieldRateVo;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.AssetUs;
import com.i000.stock.user.dao.model.IndexUs;
import com.i000.stock.user.dao.model.IndexValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class UsGainRateService {

    @Autowired
    private AssetUsService assetUsService;

    @Autowired
    private IndexUSService indexUSService;


    /**
     * 计算当天的收益率以及累计与指数的对比
     * 自己只有一天但是指数是有变化的。
     *
     * @param userCode
     * @return
     */
    public RelativeProfitBO getTodayBeatSzByUserCode(String userCode) {
        List<AssetUs> assets = assetUsService.findNewestTwoByUser(userCode);

        List<IndexUs> indexValues = indexUSService.findTwoNewest();
        BigDecimal bd;
        BigDecimal sz;
        RelativeProfitBO relativeProfitBO;
        if (assets.size() < 2 || indexValues.size() < 2) {
            bd = getRelativeProfitRate(assets.get(0), assets.get(0));
            sz = getRelativeProfitRate(indexValues.get(0).getSp500(), indexValues.get(1).getSp500());
            relativeProfitBO = RelativeProfitBO.builder()
                    .relativeProfit(getRelativeProfit(assets.get(0), assets.get(0)))
                    .relativeProfitRate(getRelativeProfitRate(assets.get(0), assets.get(0)))
                    .beatStandardRate(getBeatStandardRate(bd, sz)).build();
        } else {
            IndexUs now = indexUSService.getByDate(assets.get(0).getDate());
            IndexUs before = indexUSService.getByDate(assets.get(1).getDate());


            bd = getRelativeProfitRate(assets.get(0), assets.get(1));
            sz = getRelativeProfitRate(now.getSp500(), before.getSp500());
            relativeProfitBO = RelativeProfitBO.builder()
                    .relativeProfit(getRelativeProfit(assets.get(0), assets.get(1)))
                    .relativeProfitRate(getRelativeProfitRate(assets.get(0), assets.get(1)))
                    .beatStandardRate(getBeatStandardRate(bd, sz)).build();
        }
        return relativeProfitBO;
    }

    /**
     * 计算累计的收益情况
     *
     * @param userCode
     * @return
     */
    public RelativeProfitBO getTotalBeatByUserCode(String userCode) {
        AssetUs nowAsset = assetUsService.getNewest(userCode);
        AssetUs afterAsset = assetUsService.getOldestOneByUser(userCode);
        IndexUs after = indexUSService.getLtDateOne(LocalDate.parse("2019-02-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        IndexUs now = indexUSService.getNewestFromDB();
        BigDecimal sz = getRelativeProfitRate(now.getSp500(), after.getSp500());
        BigDecimal bd = getRelativeProfitRate(nowAsset, afterAsset);

        RelativeProfitBO relativeProfitBO = RelativeProfitBO.builder()
                .relativeProfit(getRelativeProfit(nowAsset, afterAsset))
                .relativeProfitRate(getRelativeProfitRate(nowAsset, afterAsset))
                .beatStandardRate(getBeatStandardRate(bd, sz)).build();
        return relativeProfitBO;
    }


    public HistoryProfitVO getHistory(LocalDate start, LocalDate end, String title) {
        IndexUs startIndex = indexUSService.getByDate(start, "<");
        IndexUs endIndex = indexUSService.getByDate(end, "<=");
        AssetUs startAsset = assetUsService.getByUserAndDate("10000000", startIndex.getDate());
        AssetUs endAsset = assetUsService.getByUserAndDate("10000000", endIndex.getDate());
        BigDecimal szRate = (endIndex.getSp500().subtract(startIndex.getSp500()))
                .divide(startIndex.getSp500(), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal startV = startAsset.getBalance().add(startAsset.getStock()).add(startAsset.getCover());
        BigDecimal endV = endAsset.getBalance().add(endAsset.getStock()).add(endAsset.getCover());
        BigDecimal assetRate = (endV.subtract(startV)).divide(startV, 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal beta = getBeatStandardRate(assetRate, szRate);
        return HistoryProfitVO.builder().gain(assetRate.multiply(BigDecimal.valueOf(100)))
                .szGain(szRate.multiply(BigDecimal.valueOf(100)))
                .beta(beta).title(title).build();
    }

    public HistoryProfitVO getYearRate(LocalDate end) {
        LocalDate init = LocalDate.parse("2019-02-01");
        LocalDate now = LocalDate.now();
        IndexUs startIndex = indexUSService.getByDate(init, "<");
        IndexUs endIndex = indexUSService.getByDate(now, "<=");
        Double szYearRate = getYearRate(startIndex.getSp500().doubleValue(),
                endIndex.getSp500().doubleValue(), startIndex.getDate(), endIndex.getDate());
        AssetUs startAsset = assetUsService.getByUserAndDate("10000000", startIndex.getDate());
        AssetUs endAsset = assetUsService.getByUserAndDate("10000000", endIndex.getDate());
        BigDecimal startV = startAsset.getBalance().add(startAsset.getStock()).add(startAsset.getCover());
        BigDecimal endV = endAsset.getBalance().add(endAsset.getStock()).add(endAsset.getCover());
        Double ggYearRate = getYearRate(startV.doubleValue(), endV.doubleValue(), startIndex.getDate(), endIndex.getDate());
        BigDecimal beta = getBeatStandardRate(BigDecimal.valueOf(ggYearRate / 100.0), BigDecimal.valueOf(szYearRate / 100.0));
        return HistoryProfitVO.builder()
                .gain(BigDecimal.valueOf(ggYearRate)).szGain(BigDecimal.valueOf(szYearRate))
                .beta(beta).title("平均年化").build();

    }

    /**
     * 计算年化收益率
     *
     * @param start
     * @param end
     * @param startDate
     * @param endDate
     * @return
     */
    private Double getYearRate(double start, double end, LocalDate startDate, LocalDate endDate) {
        //首先计算隔了多少年
        long diffDay = endDate.toEpochDay() - startDate.toEpochDay();
        double years = diffDay / 365.0;
        //收益率
        double gain = (end - start) / start;
        return (Math.pow(gain + 1, 1 / (years)) - 1) * 100;
    }


    /**
     * todo 时间问题需要注意一下。美国时间，这样拿应该会出问题。
     * 计算最大回撤
     *
     * @param user 用户码
     * @param diff 间隔天数  30  60  90  365
     * @return
     */
    public BigDecimal getWithdrawal(String user, Integer diff) {
        int month = diff / 30;
        LocalDate now = LocalDate.now();
        LocalDate before = now.minusMonths(month);
        return getWithdrawal(before, now, user);
    }

    public PageGainVo getRecentlyGain(String userCode, LocalDate start, String title) {
        IndexUs baseIndex = indexUSService.getLtDateOne(start);
        if (Objects.isNull(baseIndex)) {
            //如果不存在就获取第一个
            baseIndex = indexUSService.getOldestFromDB();
        }

        AssetUs baseAsset = assetUsService.getLtDateByDateAndUser(start, userCode);
        if (Objects.isNull(baseAsset)) {
            baseAsset = assetUsService.getOldestOneByUser(userCode);
        }

        return getRecentlyGain(baseIndex, baseAsset, userCode, title);
    }

    /**
     * 获取折线图
     *
     * @param userCode
     * @param date
     * @param end
     * @return
     */
    public UsYieldRateVo getIndexTrend(String userCode, LocalDate date, LocalDate end) {
        IndexUs baseIndex = indexUSService.getLtDateOne(date);
        if (Objects.isNull(baseIndex)) {
            baseIndex = indexUSService.getOldestFromDB();
        }
        LocalDate start = baseIndex.getDate();
        List<IndexUs> indexValue = indexUSService.findBetweenDate(start, end);
        List<AssetUs> asset = assetUsService.findBetweenDateByUser(start, end, userCode);

        UsYieldRateVo result = createYieldRateVo(start);
        if (!CollectionUtils.isEmpty(asset)) {
            result.getTime().clear();
            result.getTime().add(0, asset.get(0).getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));

            AssetUs baseAsset = asset.get(0);

            Map<LocalDate, List<IndexUs>> indexSort = indexValue.stream().collect(groupingBy(IndexUs::getDate));
            IndexUs indexBase = Objects.isNull(indexSort.get(baseAsset.getDate()).get(0)) ?
                    indexSort.get(baseAsset.getDate()).get(1) :
                    indexSort.get(baseAsset.getDate()).get(0);


            //还是需要按照Asset来排序
            for (int i = 1; i < asset.size(); i++) {

                List<IndexUs> indexValues = indexSort.get(asset.get(i).getDate());
                if (CollectionUtils.isEmpty(indexValues)) {
                    break;
                }
                IndexUsBo indexValueBo = calculateIndex(indexBase, indexValues.get(0));
                result.getSp500Gain().add(indexValueBo.getSp500());
                result.getNasdaqGain().add(indexValueBo.getNasdaq());
                result.getDjiGain().add(indexValueBo.getDji());
                result.getTime().add(indexValueBo.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
                result.getStockGain().add(calculateAsset(baseAsset, asset.get(i)));
            }
        }
        return result;
    }

    private UsYieldRateVo createYieldRateVo(LocalDate time) {
        //注意点就是这个日期不一定有
        UsYieldRateVo result = new UsYieldRateVo();
        result.getDjiGain().add(BigDecimal.ZERO);
        result.getNasdaqGain().add(BigDecimal.ZERO);
        result.getSp500Gain().add(BigDecimal.ZERO);
        result.getStockGain().add(BigDecimal.ZERO);
        result.getTime().add(time.format(DateTimeFormatter.ofPattern("yy-MM-dd")));
        return result;
    }

    private PageGainVo getRecentlyGain(IndexUs baseIndex, AssetUs baseAssert, String userCode, String title) {

        AssetUs valueAsset = assetUsService.getNewest(userCode);
        IndexUs value = indexUSService.getByDate(valueAsset.getDate());
        IndexUsBo indexValueBo = calculateIndex(baseIndex, value);
        List<GainVo> gainVoList = new ArrayList<>(5);

        BigDecimal bd = calculateAsset(baseAssert, valueAsset).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal sp500 = indexValueBo.getSp500().setScale(2, BigDecimal.ROUND_HALF_UP);
        gainVoList.add(GainVo.builder().indexName("勾股指数").profit(bd).build());
        gainVoList.add(GainVo.builder().indexName("标普500").profit(sp500).build());
        gainVoList.add(GainVo.builder().indexName("纳斯达克").profit(indexValueBo.getNasdaq().setScale(2, BigDecimal.ROUND_HALF_UP)).build());
        gainVoList.add(GainVo.builder().indexName("道琼斯").profit(indexValueBo.getDji().setScale(2, BigDecimal.ROUND_HALF_UP)).build());
        BigDecimal more = (bd.subtract(sp500)).divide(new BigDecimal(100).add(sp500), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
        gainVoList.add(GainVo.builder().indexName("跑赢标普500").profit(more).build());
        return PageGainVo.builder().Title(title).gain(gainVoList).build();
    }


    /**
     * @param start
     * @param end
     * @param user
     * @return
     */
    private BigDecimal getWithdrawal(LocalDate start, LocalDate end, String user) {
        AssetUs ltDateAsset = assetUsService.getLtDateByDateAndUser(start, user);
        if (Objects.isNull(ltDateAsset)) {
            ltDateAsset = assetUsService.getOldestOneByUser(user);
        }
        List<AssetUs> assets = assetUsService.findBetweenDateByUser(ltDateAsset.getDate(), end, user);
        assets.sort(Comparator.comparing(AssetUs::getDate));
        List<BigDecimal> assetPrice = assets.stream()
                .map(a -> a.getStock().add(a.getCover().add(a.getBalance()))).collect(Collectors.toList());
        return calcMaxDd(assetPrice);
    }


    /**
     * 计算回撤
     *
     * @param price
     * @return
     */
    public BigDecimal calcMaxDd(List<BigDecimal> price) {
        BigDecimal max_unit_value = price.get(0);
        BigDecimal max_dd = BigDecimal.ZERO;
        BigDecimal dd;
        for (int i = 1; i < price.size(); i++) {
            max_unit_value = price.get(i).compareTo(max_unit_value) > 0 ? price.get(i) : max_unit_value;
            dd = price.get(i).divide(max_unit_value, 4, BigDecimal.ROUND_UP).subtract(new BigDecimal(1));
            max_dd = dd.compareTo(max_dd) < 0 ? dd : max_dd;
        }
        return max_dd.multiply(new BigDecimal(100));
    }

    /**
     * 计算仓位
     *
     * @param assetUs
     * @return
     */
    public BigDecimal getPosition(AssetUs assetUs) {
        BigDecimal stock = assetUs.getStock().subtract(assetUs.getCover());
        BigDecimal total = assetUs.getBalance().add(assetUs.getStock()).add(assetUs.getCover());
        return stock.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }


    /**
     * 计算跑赢指数多少
     *
     * @param bd 系统收益率
     * @param sz 指数收益率
     * @return
     */
    private BigDecimal getBeatStandardRate(BigDecimal bd, BigDecimal sz) {
        return (bd.subtract(sz)).divide(new BigDecimal(1).add(sz),
                4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
    }

    /**
     * 计算两者的收益率
     *
     * @param now    现在的账户信息
     * @param before 之前的账户信息
     * @return
     */
    private BigDecimal getRelativeProfitRate(AssetUs now, AssetUs before) {
        BigDecimal nowAccount = now.getStock().add(now.getCover()).add(now.getBalance());
        BigDecimal beforeAccount = before.getStock().add(before.getCover()).add(before.getBalance());
        return (nowAccount.subtract(beforeAccount)).divide(beforeAccount, 4, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算收益率
     *
     * @param now    现在的总金额
     * @param before 之前的总金额
     * @return
     */
    private BigDecimal getRelativeProfitRate(BigDecimal now, BigDecimal before) {
        return (now.subtract(before)).divide(before, 4, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算收益
     *
     * @param now    现在的账户
     * @param before 之前的账户
     * @return
     */
    private BigDecimal getRelativeProfit(AssetUs now, AssetUs before) {
        BigDecimal nowAccount = now.getStock().add(now.getCover()).add(now.getBalance());
        BigDecimal beforeAccount = before.getStock().add(before.getCover()).add(before.getBalance());
        return nowAccount.subtract(beforeAccount);
    }


    private BigDecimal calculateAsset(AssetUs base, AssetUs value) {
        return Objects.isNull(base)
                ? BigDecimal.ZERO
                : getRate(base.getBalance().add(base.getStock()).add(base.getCover()), value.getBalance().add(value.getStock()).add(value.getCover()));
    }

    private IndexUsBo calculateIndex(IndexUs base, IndexUs value) {

        return IndexUsBo.builder()
                .dji(getRate(base.getDji(), value.getDji()))
                .nasdaq(getRate(base.getNasdaq(), value.getNasdaq()))
                .sp500(getRate(base.getSp500(), value.getSp500()))
                .date(value.getDate()).build();
    }

    private BigDecimal getRate(BigDecimal base, BigDecimal value) {
        return (value.subtract(base)).divide(base, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }


}

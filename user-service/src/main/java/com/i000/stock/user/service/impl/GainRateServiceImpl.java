package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexValueBo;
import com.i000.stock.user.api.entity.bo.RelativeProfitBO;
import com.i000.stock.user.api.entity.vo.GainVo;
import com.i000.stock.user.api.entity.vo.PageGainVo;
import com.i000.stock.user.api.entity.vo.YieldRateVo;
import com.i000.stock.user.api.service.buiness.AssetService;
import com.i000.stock.user.api.service.buiness.GainRateService;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.WebApiException;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.model.Asset;
import com.i000.stock.user.dao.model.IndexValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:09 2018/7/11
 * @Modified By:
 */
@Slf4j
@Service
public class GainRateServiceImpl implements GainRateService {

    @Resource
    private IndexValueService indexValueService;

    @Resource
    private AssetService assetService;

    /**
     * 查询指定时间范围内的收益率信息
     *
     * @param userCode 用户码
     * @param diff     间隔
     * @param end      结束日期  开始日期=end-diff
     * @return
     */
    @Override
    public YieldRateVo getIndexTrend(String userCode, LocalDate date, LocalDate end) {
        IndexValue baseIndex = indexValueService.getBefore(date);
        if (Objects.isNull(baseIndex)) {
            baseIndex = indexValueService.getLastOne();
        }
        LocalDate start = baseIndex.getDate();

        List<IndexValue> indexValue = indexValueService.findBetween(start, end);
        List<Asset> asset = assetService.findAssetBetween(userCode, start, end);
        YieldRateVo result = createYieldRateVo(start);
        //需要开始计算指数信息 此处会有一个千古指数
        if (!CollectionUtils.isEmpty(asset)) {
            result.getTime().clear();
            result.getTime().add(0, asset.get(0).getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));

            List<IndexValue> indexValueSort = indexValue.stream()
                    .sorted(Comparator.comparing(IndexValue::getDate)).collect(Collectors.toList());
            IndexValue indexBase = indexValueSort.get(0);
            Asset baseAsset = asset.get(0);

            Map<LocalDate, List<IndexValue>> indexSort = indexValue.stream().collect(groupingBy(IndexValue::getDate));

            //还是需要按照Asset来排序
            for (int i = 1; i < asset.size(); i++) {

                List<IndexValue> indexValues = indexSort.get(asset.get(i).getDate());
                if (CollectionUtils.isEmpty(indexValues)) {
                    break;
                }
                IndexValueBo indexValueBo = calculateIndex(indexBase, indexValues.get(0));
                result.getCybGain().add(indexValueBo.getCyb());
                result.getHsGain().add(indexValueBo.getHs());
                result.getSzGain().add(indexValueBo.getSz());
                result.getTime().add(indexValueBo.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
                result.getStockGain().add(calculateAsset(baseAsset, asset.get(i)));
            }
        }
        return result;
    }

    /**
     * @param userCode 用户码
     * @param start    间隔
     * @param title    首页显示的名称
     * @return
     */
    @Override
    public PageGainVo getRecentlyGain(String userCode, LocalDate start, String title) {
        System.out.println(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        IndexValue baseIndex = indexValueService.getBefore(start);
        if (Objects.isNull(baseIndex)) {
            baseIndex = indexValueService.getLastOne();
        }

        Asset baseAsset = assetService.getBeforeDate(start, userCode);
        if (Objects.isNull(baseAsset)) {
            baseAsset = assetService.getInit(userCode);
        }

        return getRecentlyGain(baseIndex, baseAsset, userCode, title);
    }

    private PageGainVo getRecentlyGain(IndexValue baseIndex, Asset baseAssert, String userCode, String title) {
        IndexValue value = indexValueService.getLately();
        IndexValueBo indexValueBo = calculateIndex(baseIndex, value);
        List<GainVo> gainVoList = new ArrayList<>(5);
        Asset valueAsset = assetService.getLately(userCode);
        BigDecimal bd = calculateAsset(baseAssert, valueAsset).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal sz = indexValueBo.getSz().setScale(2, BigDecimal.ROUND_HALF_UP);
        gainVoList.add(GainVo.builder().indexName("毕达指数").profit(bd).build());
        gainVoList.add(GainVo.builder().indexName("上证指数").profit(sz).build());
        gainVoList.add(GainVo.builder().indexName("创业板指").profit(indexValueBo.getCyb().setScale(2, BigDecimal.ROUND_HALF_UP)).build());
        gainVoList.add(GainVo.builder().indexName("沪深300指").profit(indexValueBo.getHs().setScale(2, BigDecimal.ROUND_HALF_UP)).build());
        //毕达指数 <--> 上证指数   毕达上涨 1%  上证跌 -2%  （1+0.01 - （1-0.02））/ （1-0.02）= (0.01 + 0.02)/(1 - 0.02)
        BigDecimal more = (bd.subtract(sz)).divide(new BigDecimal(100).add(sz), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal("100"));
        gainVoList.add(GainVo.builder().indexName("跑赢上证").profit(more).build());
        return PageGainVo.builder().Title(title).gain(gainVoList).build();
    }

    /**
     * 获取预期年化
     *
     * @param pageGainVo
     * @param date
     * @return
     */
    @Override
    public PageGainVo getYearRate(PageGainVo pageGainVo, LocalDate date) {
        int days = date.getDayOfYear();
        PageGainVo result = ConvertUtils.beanConvert(pageGainVo, new PageGainVo());
        result.setGain(deepCopy(pageGainVo.getGain()));
        BigDecimal rate = new BigDecimal(365).divide(new BigDecimal(days), 4, BigDecimal.ROUND_HALF_UP);
        for (GainVo gainVo : result.getGain()) {
            gainVo.setProfit(gainVo.getProfit().multiply(rate).setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        result.setTitle("预期年化");
        return result;
    }

    @Override
    public PageGainVo getFromYearStart(String userCode, Integer diff, LocalDate end, String title) {
        String year = end.getYear() + "-01-01";
        Asset asset = assetService.getYearFirst(year, userCode);
        IndexValue index = indexValueService.getYearFirst(year);
        return getRecentlyGain(index, asset, userCode, title);
    }

    @Override
    public RelativeProfitBO getTodayBeatSzByUserCode(String userCode) {
        List<Asset> assets = assetService.getLatelyTwoByUserCode(userCode);
        List<IndexValue> indexValues = indexValueService.getLatelyTwo();
        BigDecimal bd = getRelativeProfitRate(assets.get(0), assets.get(1));
        BigDecimal sz = getRelativeProfitRate(indexValues.get(0).getSh(), indexValues.get(1).getSh());

        RelativeProfitBO relativeProfitBO = RelativeProfitBO.builder()
                .relativeProfit(getRelativeProfit(assets.get(0), assets.get(1)))
                .relativeProfitRate(getRelativeProfitRate(assets.get(0), assets.get(1)))
                .beatStandardRate(getBeatStandardRate(bd, sz)).build();
        return relativeProfitBO;
    }

    @Override
    public RelativeProfitBO getTotalBeatByUserCode(String userCode) {
        IndexValue after = indexValueService.getLastOne();
        IndexValue now = indexValueService.getLately();
        BigDecimal sz = getRelativeProfitRate(now.getSh(), after.getSh());
        Asset nowAsset = assetService.getLately(userCode);
        Asset afterAsset = assetService.getByUserCodeAndDate(userCode, after.getDate());
        BigDecimal bd = getRelativeProfitRate(nowAsset, afterAsset);

        RelativeProfitBO relativeProfitBO = RelativeProfitBO.builder()
                .relativeProfit(getRelativeProfit(nowAsset, afterAsset))
                .relativeProfitRate(getRelativeProfitRate(nowAsset, afterAsset))
                .beatStandardRate(getBeatStandardRate(bd, sz)).build();
        return relativeProfitBO;
    }


    private YieldRateVo createYieldRateVo(LocalDate time) {
        //注意点就是这个日期不一定有
        YieldRateVo result = new YieldRateVo();
        result.getHsGain().add(BigDecimal.ZERO);
        result.getSzGain().add(BigDecimal.ZERO);
        result.getCybGain().add(BigDecimal.ZERO);
        result.getStockGain().add(BigDecimal.ZERO);
        result.getTime().add(time.format(DateTimeFormatter.ofPattern("yy-MM-dd")));
        return result;
    }

    private BigDecimal calculateAsset(Asset base, Asset value) {
        return Objects.isNull(base)
                ? BigDecimal.ZERO
                : getRate(base.getBalance().add(base.getStock()).add(base.getCover()), value.getBalance().add(value.getStock()).add(value.getCover()));
    }

    private IndexValueBo calculateIndex(IndexValue base, IndexValue value) {

        return IndexValueBo.builder()
                .cyb(getRate(base.getCyb(), value.getCyb()))
                .hs(getRate(base.getHs(), value.getHs()))
                .sz(getRate(base.getSh(), value.getSh()))
                .date(value.getDate()).build();
    }

    private BigDecimal getRate(BigDecimal base, BigDecimal value) {
        return (value.subtract(base)).divide(base, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
    }

    public static <T> List<T> deepCopy(List<T> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new WebApiException(ApplicationErrorMessage.SERVER_ERROR.getCode(), "数据拷贝错误");
    }

    private BigDecimal getBeatStandardRate(BigDecimal bd, BigDecimal sz) {
        return (bd.subtract(sz)).divide(new BigDecimal(1).add(sz),
                4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
    }

    private BigDecimal getRelativeProfitRate(Asset now, Asset before) {
        BigDecimal nowAccount = now.getStock().add(now.getCover()).add(now.getBalance());
        BigDecimal beforeAccount = before.getStock().add(before.getCover()).add(before.getBalance());
        return (nowAccount.subtract(beforeAccount)).divide(beforeAccount, 4, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getRelativeProfitRate(BigDecimal now, BigDecimal before) {
        return (now.subtract(before)).divide(before, 4, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal getRelativeProfit(Asset now, Asset before) {
        BigDecimal nowAccount = now.getStock().add(now.getCover()).add(now.getBalance());
        BigDecimal beforeAccount = before.getStock().add(before.getCover()).add(before.getBalance());
        return nowAccount.subtract(beforeAccount);
    }

}

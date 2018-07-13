package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexValueBo;
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
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    public YieldRateVo getIndexTrend(String userCode, Integer diff, LocalDate end) {
        LocalDate start = end.minusDays(diff);
        List<IndexValue> indexValue = indexValueService.findBetween(start, end);
        List<Asset> asset = assetService.findAssetBetween(userCode, start, end);
        YieldRateVo result = createYieldRateVo(start);
        //需要开始计算指数信息 此处会有一个千古指数
        if (!CollectionUtils.isEmpty(asset)) {
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
     * @param diff     间隔
     * @param end      结束日期   开始日期=end-diff
     * @param title    首页显示的名称
     * @return
     */
    @Override
    public PageGainVo getRecentlyGain(String userCode, Integer diff, LocalDate end, String title) {
        LocalDate start = end.minusDays(diff);
        //此处的base可能需要修改--->需要改成今年以来

        IndexValue base = indexValueService.getRecently(start);
        IndexValue value = indexValueService.getLately();
        IndexValueBo indexValueBo = calculateIndex(base, value);
        List<GainVo> gainVoList = new ArrayList<>(4);
        Asset baseAsset = assetService.getDiff(start, userCode);
        Asset valueAsset = assetService.getLately(userCode);
        gainVoList.add(GainVo.builder().indexName("千古指数").profit(calculateAsset(baseAsset, valueAsset).setScale(2, BigDecimal.ROUND_UP)).build());
        gainVoList.add(GainVo.builder().indexName("上证指数").profit(indexValueBo.getSz().setScale(2, BigDecimal.ROUND_UP)).build());
        gainVoList.add(GainVo.builder().indexName("创业板指").profit(indexValueBo.getCyb().setScale(2, BigDecimal.ROUND_UP)).build());
        gainVoList.add(GainVo.builder().indexName("沪深300指").profit(indexValueBo.getHs().setScale(2, BigDecimal.ROUND_UP)).build());
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
        BigDecimal rate = new BigDecimal(365).divide(new BigDecimal(days), 4, BigDecimal.ROUND_UP);
        for (GainVo gainVo : result.getGain()) {
            gainVo.setProfit(gainVo.getProfit().multiply(rate).setScale(2, BigDecimal.ROUND_UP));
        }
        result.setTitle("预期年化");
        return result;
    }

    @Override
    public PageGainVo getFromYearStart(String userCode, Integer diff, LocalDate end, String title) {
        //动态调整   今年的多少天
        int days = end.getDayOfYear();
        int diff_2 = days > diff ? days : days;
        return getRecentlyGain(userCode, diff_2, end, title);
    }


    private YieldRateVo createYieldRateVo(LocalDate time) {
        YieldRateVo result = new YieldRateVo();
        result.getHsGain().add(BigDecimal.ZERO);
        result.getSzGain().add(BigDecimal.ZERO);
        result.getCybGain().add(BigDecimal.ZERO);
        result.getStockGain().add(BigDecimal.ZERO);
        result.getTime().add(time.format(DateTimeFormatter.ofPattern("yy-MM-dd")));
        return result;
    }

    private BigDecimal calculateAsset(Asset base, Asset value) {
        return getRate(base.getBalance().add(base.getStock()).add(base.getCover()),
                value.getBalance().add(value.getStock()).add(value.getCover()));
    }

    private IndexValueBo calculateIndex(IndexValue base, IndexValue value) {

        return IndexValueBo.builder()
                .cyb(getRate(base.getCyb(), value.getCyb()))
                .hs(getRate(base.getHs(), value.getHs()))
                .sz(getRate(base.getSh(), value.getSh()))
                .date(value.getDate()).build();
    }

    private BigDecimal getRate(BigDecimal base, BigDecimal value) {
        return (value.subtract(base)).divide(base, 6, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
    }

    public static <T> List<T> deepCopy(List<T> src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
//        @SuppressWarnings("unchecked")
            List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new WebApiException(ApplicationErrorMessage.SERVER_ERROR.getCode(), "数据拷贝错误");
    }
}

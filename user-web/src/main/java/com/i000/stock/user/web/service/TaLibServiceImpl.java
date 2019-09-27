package com.i000.stock.user.web.service;

import com.i000.stock.user.api.entity.bo.JQKlineBo;
import com.i000.stock.user.api.entity.bo.MacdTaLibBo;
import com.i000.stock.user.api.entity.bo.PriceAmountChangeBO;
import com.i000.stock.user.api.entity.constant.PeriodEnum;
import com.i000.stock.user.service.impl.CompanyCrawlerServiceImpl;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class TaLibServiceImpl {

    @Autowired
    private Core core;

    @Autowired
    private CompanyCrawlerServiceImpl companyCrawlerService;

    /**
     * 返回值为MACD的值,最新的值为0号位,
     * diff与dea的值为正常值
     * macd的值为原值的一半
     *
     * @param symbol
     * @param period
     * @return
     */
    public MacdTaLibBo getMacd(String symbol, PeriodEnum period) {
        List<JQKlineBo> kLine = companyCrawlerService.findKLine(symbol, period, 300);
        if (CollectionUtils.isEmpty(kLine)) {
            return null;
        }
        double[] doubles = kLine.stream().mapToDouble(a -> a.getClose()).toArray();
        int size = doubles.length;
        double diff[] = new double[300];
        double dea[] = new double[300];
        double macd[] = new double[300];
        MInteger outBegIdx = new MInteger();
        outBegIdx.value = 0;
        MInteger outNBElement = new MInteger();
        outNBElement.value = 0;
        core.macd(20, size - 1, doubles, 12, 26, 9,
                outBegIdx, outNBElement, diff, dea, macd);
        return MacdTaLibBo.builder().dea(arrayToList(dea)).diff(arrayToList(diff)).macd(arrayToList(macd)).build();
    }

    /**
     * 计算股票当天的涨跌幅
     * 成交量当月的变化率
     *
     * @param code
     * @return
     */
    public PriceAmountChangeBO getChange(String code) {
        List<JQKlineBo> kLine = companyCrawlerService.findKLine(code, PeriodEnum.DAY_1, 21);
        if (!CollectionUtils.isEmpty(kLine)) {
            PriceAmountChangeBO result = new PriceAmountChangeBO();
            kLine.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            JQKlineBo now = kLine.get(0);
            JQKlineBo before = kLine.get(1);
            double asDouble = kLine.stream().mapToDouble(a -> a.getVolume()).average().getAsDouble();
            result.setPrice((now.getClose() - before.getClose()) / before.getClose() * 100);
            if (asDouble > 0) {
                result.setAmount((now.getVolume() - asDouble) / asDouble * 100);
            }
            return result;
        }
        return null;
    }

    private List<Double> arrayToList(double array[]) {
        List<Double> result = new ArrayList<>(array.length);
        for (double v : array) {
            result.add(v);
        }
        Collections.reverse(result);
        Iterator<Double> it = result.iterator();
        while (it.hasNext()) {
            Double next = it.next();
            if (next != 0.0) {
                break;
            }
            it.remove();
        }
        return result;
    }


}

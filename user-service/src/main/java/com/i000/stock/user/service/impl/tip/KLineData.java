package com.i000.stock.user.service.impl.tip;

import com.alibaba.fastjson.JSON;
import com.i000.stock.user.api.entity.bo.BollBo;
import com.i000.stock.user.api.entity.bo.KLineBO;
import com.i000.stock.user.api.entity.bo.MacdBo;
import com.i000.stock.user.api.entity.constant.ChangeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class KLineData {

    @Autowired
    private RestTemplate restTemplate;

    // String 由 code+msg 组成
    public Map<String, KLineBO> BUY_MAP = new HashMap();

    private static final String URL = "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData";

    public List<KLineBO> getKLine(Integer scale, Integer datalen, String symbol) {
        String param = "?symbol=" + symbol + "&scale=" + scale + "&ma=no&datalen=" + datalen + "#";
        String resultStr = restTemplate.getForObject(URL + param, String.class);
        String jsonStr = resultStr.replaceAll("day", "\"day\"")
                .replaceAll("open", "\"open\"")
                .replaceAll("high", "\"high\"")
                .replaceAll("low", "\"low\"")
                .replaceAll("close", "\"close\"")
                .replaceAll("volume", "\"volume\"");
        return JSON.parseArray(jsonStr, KLineBO.class);
    }

//    针对当天计划买入的提示：

//    1. 价格逼近120均线  +-0.5%

//	  3. macd dif转正数 / macd黄金交叉

//	  5. 跌幅高于 4% 及以上

    /**
     * 价格逼近120均线  +-0.5%
     *
     * @param kline
     * @param rate  如果是逼近 0.3% 则 rate 传递 0.3
     * @return
     */
    public boolean isNearOf120(List<KLineBO> kline, BigDecimal rate) {
        rate = (new BigDecimal(100).subtract(rate)).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP);
        kline.sort((a, b) -> b.getDay().compareTo(a.getDay()));
        List<KLineBO> kLineBOS = kline.subList(0, 120);
        List<KLineBO> kLineBOS60 = kline.subList(0, 60);
        double close120 = kLineBOS.stream().mapToDouble((x) -> x.getClose().doubleValue()).average().getAsDouble();
        double close60 = kLineBOS60.stream().mapToDouble((x) -> x.getClose().doubleValue()).average().getAsDouble();
        if (close120 >= close60) {
            return false;
        }
        BigDecimal close = kLineBOS.get(0).getClose();
        //如果在120 上方，也就是需要减小
        BigDecimal m120 = new BigDecimal(close120);
        BigDecimal temp = close.compareTo(m120) >= 0 ? close.multiply(rate) : m120.multiply(rate);
        return close.compareTo(m120) >= 0 ? temp.compareTo(m120) <= 0 : close.compareTo(temp) >= 0;
    }


    public BollBo getBoll(List<KLineBO> kline) {
        return getBoll(kline, 20, 2);
    }

    public BigDecimal getCCI(List<KLineBO> kline) {
        return getCCI(kline, 14);
    }

    public MacdBo getMACD(List<KLineBO> kline) {
        return getMACD(kline, 9, 26, 12);
    }

    /**
     * 计算boll指标
     *
     * @param kline    需要是按照时间大小倒序的
     * @param len      长度，惯例为 20
     * @param multiple 方差的倍数，惯例为 2
     * @return
     */
    private BollBo getBoll(List<KLineBO> kline, Integer len, Integer multiple) {
        kline.sort((a, b) -> b.getDay().compareTo(a.getDay()));
        List<BigDecimal> closes = kline.stream().map(a -> a.getClose()).collect(Collectors.toList());
        System.out.println(kline.get(0).getDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        closes = closes.subList(0, len);
        BigDecimal avg = new BigDecimal(closes.stream().mapToDouble(a -> a.doubleValue()).average().getAsDouble())
                .setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal std2 = getStd(closes, avg).multiply(new BigDecimal(multiple));
        return BollBo.builder().mid(avg).close(kline.get(0).getClose()).low(avg.subtract(std2)).up(avg.add(std2))
                .lowPrice(kline.get(0).getLow()).build();
    }


    /**
     * 计算CCI指标值，用来评估超买超卖
     *
     * @param kline
     * @param kNumber 惯例为14
     * @return
     */
    private BigDecimal getCCI(List<KLineBO> kline, Integer kNumber) {
        kline.sort((a, b) -> b.getDay().compareTo(a.getDay()));
        List<KLineBO> kLineBOS = kline.subList(0, kNumber);
        KLineBO nowKline = kLineBOS.get(0);
        BigDecimal tp = (nowKline.getClose().add(nowKline.getHigh()).add(nowKline.getLow())).divide(new BigDecimal(3), 4, BigDecimal.ROUND_HALF_UP);
        List<BigDecimal> mas = kLineBOS.stream().map(a -> a.getLow().add(a.getHigh()).add(a.getClose()).divide(new BigDecimal(3), 4, BigDecimal.ROUND_HALF_UP)).collect(Collectors.toList());
        BigDecimal ma = new BigDecimal(mas.stream().mapToDouble(a -> a.doubleValue()).average().getAsDouble()).setScale(4, BigDecimal.ROUND_HALF_UP);
        BigDecimal md = new BigDecimal(mas.stream().mapToDouble(a -> (a.subtract(ma)).abs().doubleValue()).average().getAsDouble());
        if (tp.compareTo(ma) == 0) {
            return BigDecimal.ZERO;
        }
        return (tp.subtract(ma)).divide(md, 8, BigDecimal.ROUND_HALF_UP)
                .divide(new BigDecimal(0.015), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * @param kline       注意是按照时间升序排列的序列
     * @param shortPeriod 短周期 惯例为 9
     * @param longPeriod  长周期 惯例为 26
     * @param midPeriod   中间值 惯例为 12
     * @return
     */
    private MacdBo getMACD(List<KLineBO> kline, final int shortPeriod, final int longPeriod, int midPeriod) {
        kline.sort(Comparator.comparing(KLineBO::getDay));
        List<Double> list = kline.stream().map(a -> a.getClose().doubleValue()).collect(Collectors.toList());
        List<Double> diffList = new ArrayList<>();
        Double shortEMA;
        Double longEMA;
        Double dif = 0.0;
        Double dea;

        for (int i = list.size() - 1; i >= 0; i--) {
            List<Double> sublist = list.subList(0, list.size() - i);
            shortEMA = getEXPMA(sublist, shortPeriod);
            longEMA = getEXPMA(sublist, longPeriod);
            dif = shortEMA - longEMA;
            diffList.add(dif);
        }
        dea = getEXPMA(diffList, midPeriod);
        return MacdBo.builder().dea(new BigDecimal(dea))
                .dif(new BigDecimal(dif))
                .macd(new BigDecimal((dif - dea) * 2)).build();
    }

    private Double getEXPMA(final List<Double> list, final int number) {
        Double k = 2.0 / (number + 1.0);
        Double ema = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            ema = list.get(i) * k + ema * (1 - k);
        }
        return ema;
    }

    private BigDecimal getStd(List<BigDecimal> close, BigDecimal average) {
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < close.size(); i++) {
            BigDecimal temp = close.get(i).subtract(average);
            total = total.add(temp.multiply(temp));
        }
        double std = Math.sqrt((total.divide(new BigDecimal(close.size()),
                4, BigDecimal.ROUND_HALF_UP)).doubleValue());
        return new BigDecimal(std).setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 此处只返回买入信号，但是买入不允许太频繁。
     *
     * @param kLine
     * @return
     */
    public KLineBO     buy(List<KLineBO> kLine, String symbol) {
//        symbol = symbol.startsWith("60") ? "sh" + symbol : "sz" + symbol;
//        List<KLineBO> kLine = getKLine(5, 1024, symbol);
        BollBo boll = getBoll(kLine);
        if (boll.getLow().compareTo(boll.getUp()) < 0 && boll.getLowPrice().compareTo(boll.getLow()) <= 0) {
            BigDecimal cci = getCCI(kLine);
            if (cci.compareTo(new BigDecimal(-100)) <= 0) {
                return getPrice(kLine, "CCI", symbol);
            }
        }
        ChangeEnum macdChange = getMacdChange(kLine);
        if (ChangeEnum.MINUS_PLUS.equals(macdChange)) {

            return getPrice(kLine, "MACD", symbol);
        }

        if (isNearOf120(kLine, new BigDecimal(0.3))) {
            return getPrice(kLine, "120", symbol);
        }


        return null;
    }

    /**
     * @param kLine
     * @return 状态改变的瞬态已经获取
     */
    public ChangeEnum getMacdChange(List<KLineBO> kLine) {
        kLine.sort(Comparator.comparing(KLineBO::getDay));
        List<MacdBo> result = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            result.add(getMACD(kLine.subList(0, kLine.size() - i)));
        }
        for (int i = 0; i < result.size() - 1; i++) {
            BigDecimal one = result.get(i).getMacd().setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal two = result.get(i + 1).getMacd().setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal temp = one.multiply(two);
            if (temp.compareTo(BigDecimal.ZERO) <= 0) {
                return one.compareTo(BigDecimal.ZERO) >= 0 ? ChangeEnum.MINUS_PLUS : ChangeEnum.PLUS_MINUS;
            }
        }
        return result.get(0).getMacd().compareTo(BigDecimal.ZERO) >= 0
                ? ChangeEnum.PLUS_NO_CHANGE : ChangeEnum.MINUS_NO_CHANGE;
    }

    private KLineBO getPrice(List<KLineBO> kLine, String msg, String symbol) {
        kLine.sort(Comparator.comparing(KLineBO::getDay));
        KLineBO kLineBO = kLine.get(kLine.size() - 1);
        kLineBO.setMsg(String.format("%s BUY ...%s", symbol, msg));
        if (BUY_MAP.containsKey(msg)) {
            //当前时间减去30分钟还要大于之前的时间即可
            if (kLineBO.getDay().minusMinutes(30).compareTo(BUY_MAP.get(msg).getDay()) < 0) {
                return null;
            }
        }
        BUY_MAP.put(kLineBO.getMsg(), kLineBO);
        return kLineBO;
    }
}

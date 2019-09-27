package com.i000.stock.user.web.service;

import com.i000.stock.user.api.entity.bo.MacdTaLibBo;
import com.i000.stock.user.api.entity.bo.PriceAmountChangeBO;
import com.i000.stock.user.api.entity.constant.PeriodEnum;
import com.i000.stock.user.api.entity.vo.RankVo;
import com.i000.stock.user.dao.mapper.StockFocusMapper;
import com.i000.stock.user.dao.model.StockFocus;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.service.impl.RankExplainService;
import com.i000.stock.user.service.impl.UserStockService;
import com.i000.stock.user.service.impl.external.StockChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StockFocusService {

    @Autowired
    private StockFocusMapper stockFocusMapper;

    @Autowired
    private UserStockService userStockService;

    @Autowired
    private RankExplainService rankExplainService;

    @Autowired
    private StockChangeService stockChangeService;


    @Autowired
    private TaLibServiceImpl taLibService;

    public List<StockFocus> find() {
        return stockFocusMapper.selectList(null);
    }

    public void save(String user) {
        stockFocusMapper.truncate();
        List<StockFocus> stockFocus = find(user);
        for (StockFocus stockFocus1 : stockFocus) {
            stockFocusMapper.insert(stockFocus1);
        }
    }


    private List<StockFocus> find(String user) {
        List<RankVo> rankVos = userStockService.findStockByUser(user);
        if (!CollectionUtils.isEmpty(rankVos)) {
            addStockChange(rankVos);
            List<StockFocus> result = ConvertUtils.listConvert(rankVos, StockFocus.class);
            for (StockFocus stockFocusVO : result) {
                MacdTaLibBo dayMacd = taLibService.getMacd(stockFocusVO.getCode(), PeriodEnum.DAY_1);
                MacdTaLibBo weekMacd = taLibService.getMacd(stockFocusVO.getCode(), PeriodEnum.WEEK_1);
                stockFocusVO.setDay(calculateMacdScore(dayMacd));
                stockFocusVO.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd")));
                stockFocusVO.setWeek(calculateMacdScore(weekMacd));
                //价格涨跌幅 ,成交量的变化率
                PriceAmountChangeBO change = taLibService.getChange(stockFocusVO.getCode());
                if (Objects.nonNull(change)) {
                    stockFocusVO.setPrice(change.getPrice());
                    stockFocusVO.setVolume(change.getAmount());
                }
            }
            return result;
        }
        return new ArrayList<>(0);
    }

    private void addStockChange(List<RankVo> rankVos) {
        for (RankVo rankVo : rankVos) {
            rankExplainService.addStockChangeInfo(rankVo);
            String url = String.format("https://xueqiu.com/S/%s", rankVo.getCode().startsWith("6")
                    ? "SH" + rankVo.getCode() : "SZ" + rankVo.getCode());
            String changeStockUrl = String.format("https://xueqiu.com/snowman/S/%s/detail#/INSIDER",
                    rankVo.getCode().startsWith("6") ? "SH" + rankVo.getCode() : "SZ" + rankVo.getCode());
            rankVo.setUrl(url);
            rankVo.setChangeStock(stockChangeService.getChangeNumber(rankVo.getCode()));
            rankVo.setChangeStockUrl(changeStockUrl);
        }
    }


    private Double calculateMacdScore(MacdTaLibBo macd) {
        if (Objects.nonNull(macd)) {
            List<Double> macd1 = macd.getMacd();
            Double now = macd1.get(0);
            Double before = macd1.get(1);
            if (now * before <= 0) {
                if (now >= 0) {
                    //金叉
                    return 100.0;
                } else if (before >= 0) {
                    //死叉
                    return 0.0;
                }
            } else {
                double result = ((now - before) / 2) * 100;
                return 50 + result;
            }
        }
        return null;
    }
}

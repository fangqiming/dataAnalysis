package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.bo.Price;
import com.i000.stock.user.api.entity.vo.EverydayStockVO;
import com.i000.stock.user.api.entity.vo.PageResultVO;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.mapper.EverydayStockMapper;
import com.i000.stock.user.dao.model.EverydayStock;
import com.i000.stock.user.dao.model.Rank;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EverydayStockService {

    @Autowired
    private EverydayStockMapper everydayStockMapper;

    @Autowired
    private ExternalServiceImpl externalService;

    @Autowired
    private RankService rankService;

    /**
     * 获取每日勾股的列表展示页
     *
     * @return
     */
    public PageResultVO<EverydayStockVO> find() {
        PageResultVO<EverydayStockVO> result = new PageResultVO();
        EntityWrapper<EverydayStock> ew = new EntityWrapper<>();
        ew.orderBy("date", false);
        List<EverydayStock> everydayStocks = everydayStockMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(everydayStocks)) {
            List<EverydayStockVO> everydayStockVOS = ConvertUtils.listConvert(everydayStocks, EverydayStockVO.class);
            everydayStockVOS.forEach(a -> a.setRate((a.getNewPrice().subtract(a.getOldPrice()))
                    .divide(a.getOldPrice(), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100))));
            result.setList(everydayStockVOS);
            result.setDate(everydayStockVOS.get(0).getDate());
        }
        return result;
    }

    /**
     * 每天收盘后 即 3:15来更新每日一个的数据
     */
    public void updateClose() {
        /**
         * 1.选出全部的股票代码
         * 2.获取股票价格
         * 3.逐一更新股票价格
         */
        List<EverydayStock> everydayStocks = everydayStockMapper.selectList(null);
        if (!CollectionUtils.isEmpty(everydayStocks)) {
            List<String> codes = everydayStocks.stream()
                    .map(a -> a.getCode()).collect(Collectors.toList());
            List<Price> price = externalService.getPrice(codes);
            Map<String, List<Price>> priceMap = price.stream().collect(Collectors.groupingBy(Price::getCode));
            for (EverydayStock everydayStock : everydayStocks) {
                //设置股票的价格
                BigDecimal close = priceMap.get(everydayStock.getCode()).get(0).getPrice();
                everydayStockMapper.updateCloseByCode(close, everydayStock.getCode());
            }
        }

    }

    /**
     * 每天更新rank的时候进行插入。
     * 现在需要插入操作
     */
    private void save(EverydayStock everydayStock, Integer number) {
        EntityWrapper<EverydayStock> ew = new EntityWrapper<>();
        ew.orderBy("date", true);
        List<EverydayStock> everydayStocks = everydayStockMapper.selectList(ew);

        if (!(CollectionUtils.isEmpty(everydayStocks) || everydayStocks.size() < number)) {
            everydayStockMapper.deleteById(everydayStocks.get(0).getId());
        }
        if (!CollectionUtils.isEmpty(everydayStocks)) {
            if (everydayStocks.get(everydayStocks.size() - 1).getDate().compareTo(everydayStock.getDate()) >= 0) {
                return;
            }
        }


        everydayStockMapper.insert(everydayStock);
        //插入新的每日勾股之后，就立马获取收盘价
        updateClose();
    }

    /**
     * 得分排名，也就是推荐的股票
     *
     * @param number 保存的数量，默认保存数量为 5天的每日推荐
     */
    public void save(Integer number) {
        Rank rank = rankService.getEverydayStock();
        if (!Objects.isNull(rank)) {
            EverydayStock everydayStock = new EverydayStock();
            everydayStock.setCode(rank.getCode());
            everydayStock.setDate(rank.getDate());
            List<String> codes = Arrays.asList(rank.getCode());
            List<Price> price = externalService.getPrice(codes);
            everydayStock.setOldPrice(price.get(0).getPrice());
            everydayStock.setNewPrice(price.get(0).getPrice());
            everydayStock.setName(price.get(0).getName());
            save(everydayStock, number);
        }
    }

    public List<String> findInEveryStock() {
        List<EverydayStock> everydayStocks = everydayStockMapper.selectList(null);
        if (!CollectionUtils.isEmpty(everydayStocks)) {
            return everydayStocks.stream().map(a -> a.getCode()).collect(Collectors.toList());
        }
        return new ArrayList<>(0);
    }

    public String getEverydayStock() {
        EntityWrapper<EverydayStock> ew = new EntityWrapper<>();
        ew.orderBy("date", false).last("limit 1");
        List<EverydayStock> everydayStocks = everydayStockMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(everydayStocks)) {
            return everydayStocks.get(0).getCode();
        } else {
            Rank everydayStock = rankService.getEverydayStock();
            if (Objects.nonNull(everydayStock)) {
                return everydayStock.getCode();
            }
        }
        return "";
    }
}

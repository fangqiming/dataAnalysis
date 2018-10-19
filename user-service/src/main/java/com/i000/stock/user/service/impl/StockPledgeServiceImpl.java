package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.vo.StockPledgeVo;
import com.i000.stock.user.api.service.external.StockPledgeService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.mapper.StockPledgeMapper;
import com.i000.stock.user.dao.model.StockPledge;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:40 2018/7/17
 * @Modified By:
 */

@Slf4j
@Component
public class StockPledgeServiceImpl implements StockPledgeService {

    @Autowired
    private StockPledgeMapper stockPledgeMapper;

    private String shUrl = "http://www.chinaclear.cn/cms-rank/queryPledgeProportion?action=query";

    @Override
    public List<StockPledge> save() throws Exception {
        List<StockPledge> stockPledges = new ArrayList<>(4000);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
//        String date = "2018.08.17";
        for (int page = 1; page < 400; page++) {
            String query = String.format("&queryDate=%s&page=%d", date, page);
            Document doc = Jsoup.connect(shUrl + query).get();
            Elements div = doc.getElementsByClass("Stock");
            if (isNotNewData(page, div)) {
                return null;
            } else if (page == 1) {
                stockPledgeMapper.truncate();
            }
            for (Element element : div) {
                Elements trs = element.child(0).child(0).children();
                for (Element tr : trs) {
                    if (!tr.hasClass("TitleBg03")) {
                        StockPledge stockPledge = StockPledge.builder()
                                .date(LocalDate.parse(tr.child(0).text().split(" ")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                                .code(tr.child(1).text())
                                .name(tr.child(2).text())
                                .pledgeNumber(Integer.valueOf(tr.child(3).text()))
                                .unlimitedPledge(new BigDecimal(tr.child(4).text()))
                                .limitedPledge(new BigDecimal(tr.child(5).text()))
                                .total(new BigDecimal(tr.child(6).text()))
                                .rate(new BigDecimal(tr.child(7).text())).build();
                        stockPledgeMapper.insert(stockPledge);
                    }
                }

            }
            //防止数据被反爬虫
            sleep(2);
        }
        return stockPledges;
    }

    @Override
    public Page<StockPledgeVo> search(BaseSearchVo baseSearchVo, String code, String name) {
        baseSearchVo.setStart();
        List<StockPledge> recode = stockPledgeMapper.search(baseSearchVo, code, name);
        Long total = stockPledgeMapper.pageTotal();
        Page<StockPledgeVo> result = new Page<>();
        List<StockPledgeVo> tradeRecordVos = ConvertUtils.listConvert(recode, StockPledgeVo.class);
        result.setList(tradeRecordVos);
        result.setTotal(total);
        return result;
    }

    private boolean isNotNewData(int page, Elements div) {
        try {
            return page == 1 && div.get(0).child(0).child(0).children().size() < 2;
        } catch (NullPointerException e) {
            log.warn("获取股权质押数据出现空指针异常，请注意是否上证交易所修改了数据结构");
        }
        return true;
    }


    private void sleep(Integer second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

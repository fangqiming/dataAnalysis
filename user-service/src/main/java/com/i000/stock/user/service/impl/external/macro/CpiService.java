package com.i000.stock.user.service.impl.external.macro;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.CpiMapper;
import com.i000.stock.user.dao.model.Cpi;
import com.i000.stock.user.dao.model.NewStockAccount;
import com.i000.stock.user.dao.model.Pmi;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CpiService {

    private String URL = "http://data.eastmoney.com/cjsj/cpi.html";

    @Autowired
    private CpiMapper cpiMapper;

    public List<Cpi> find() {
        EntityWrapper<Cpi> ew = new EntityWrapper<>();
        ew.orderBy("date");
        return cpiMapper.selectList(ew);
    }

    public void save() {
        List<Cpi> fromNet = getFromNet();
        if (!CollectionUtils.isEmpty(fromNet)) {
            cpiMapper.truncate();
            for (Cpi cpi : fromNet) {
                cpiMapper.insert(cpi);
            }
        }
    }

    private List<Cpi> getFromNet() {
        List<Cpi> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(URL).get();
            Element tb = doc.getElementById("tb");
            Elements trs = tb.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements td = tr.getElementsByTag("td");
                if (td.size() > 12) {
                    String date = PmiService.formate(td.get(0).text());
                    result.add(Cpi.builder().date(date)
                            .total(new BigDecimal(td.get(1).text()))
                            .city(new BigDecimal(td.get(5).text()))
                            .countryside(new BigDecimal(td.get(9).text())).build());
                }
            }

        } catch (Exception e) {

        }
        return result;
    }

}

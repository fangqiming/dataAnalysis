package com.i000.stock.user.service.impl.external.macro;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.PpiMapper;
import com.i000.stock.user.dao.model.Ppi;
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
public class PpiService {

    private String URL = "http://data.eastmoney.com/cjsj/ppi.html";

    @Autowired
    private PpiMapper ppiMapper;

    public List<Ppi> find() {
        EntityWrapper<Ppi> ew = new EntityWrapper<>();
        ew.orderBy("date");
        return ppiMapper.selectList(ew);
    }

    public void save() {
        List<Ppi> fromNet = getFromNet();
        if (!CollectionUtils.isEmpty(fromNet)) {
            ppiMapper.truncate();
            for (Ppi ppi : fromNet) {
                ppiMapper.insert(ppi);
            }
        }
    }

    private List<Ppi> getFromNet() {
        List<Ppi> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(URL).get();
            Element tb = doc.getElementById("tb");
            Elements trs = tb.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements td = tr.getElementsByTag("td");
                if (td.size() > 0) {
                    String date = PmiService.formate(td.get(0).text());
                    result.add(Ppi.builder().date(date).month(new BigDecimal(td.get(1).text()))
                            .total(new BigDecimal(td.get(3).text())).build());
                }
            }
        } catch (Exception e) {

        }
        return result;
    }


}

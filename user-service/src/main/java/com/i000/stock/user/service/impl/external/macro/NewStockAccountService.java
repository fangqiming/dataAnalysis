package com.i000.stock.user.service.impl.external.macro;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.NewStockAccountMapper;
import com.i000.stock.user.dao.model.NewStockAccount;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.text.html.parser.Entity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewStockAccountService {

    private String URL = "http://data.eastmoney.com/cjsj/yzgptjnew.html";

    @Autowired
    private NewStockAccountMapper newStockAccountMapper;

    public List<NewStockAccount> find() {
        EntityWrapper<NewStockAccount> ew = new EntityWrapper<>();
        ew.orderBy("date");
        return newStockAccountMapper.selectList(ew);
    }

    public void save() {
        List<NewStockAccount> fromNet = getFromNet();
        if (!CollectionUtils.isEmpty(fromNet)) {
            newStockAccountMapper.truncate();
            for (NewStockAccount newStockAccount : fromNet) {
                newStockAccountMapper.insert(newStockAccount);
            }
        }
    }

    private List<NewStockAccount> getFromNet() {
        List<NewStockAccount> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(URL).get();
            Element tb = doc.getElementById("tb");
            Elements trs = tb.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements td = tr.getElementsByTag("td");
                if (td.size() > 0) {
                    String date = td.get(0).text().substring(2, 10);
                    result.add(NewStockAccount.builder().date(date).amount(new BigDecimal(td.get(1).text())).build());
                }
            }

        } catch (Exception e) {

        }
        return result;
    }

}

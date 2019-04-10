package com.i000.stock.user.service.impl.external.macro;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.dao.mapper.PmiMapper;
import com.i000.stock.user.dao.model.Cpi;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PmiService {

    @Autowired
    private PmiMapper pmiMapper;

    private String URL = "http://data.eastmoney.com/cjsj/pmi.html";

    public List<Pmi> find() {
        EntityWrapper<Pmi> ew = new EntityWrapper<>();
        ew.orderBy("date");
        return pmiMapper.selectList(ew);
    }

    public void save() {
        List<Pmi> pmiFromNet = getPmiFromNet();
        if (!CollectionUtils.isEmpty(pmiFromNet)) {
            pmiMapper.truncate();
            for (Pmi pmi : pmiFromNet) {
                pmiMapper.insert(pmi);
            }
        }
    }

    public List<Pmi> getPmiFromNet() {
        List<Pmi> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(URL).get();
            Element table = doc.getElementById("tb");
            Elements trs = table.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                if (tds.size() > 0) {
                    result.add(Pmi.builder().date(formate(tds.get(0).text()))
                            .industry(new BigDecimal(tds.get(1).text()))
                            .noIndustry(new BigDecimal(tds.get(3).text())).build());
                }
            }
        } catch (Exception e) {

        }
        return result;
    }

    public static String formate(String date) {
        String reg = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(date);
        return mat.replaceAll("-").substring(2, 7);
    }

}

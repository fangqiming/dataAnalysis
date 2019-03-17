package com.i000.stock.user.service.impl.external.material;

import com.i000.stock.user.dao.model.Material;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 原材料的价格获取服务
 */
@Slf4j
@Service
public class MaterialPriceService {

    @Autowired
    private MaterialService materialService;

    private static final String URL = "http://www.100ppi.com/monitor/";

    public void savePrice() {
        try {
            Document doc;
            DateTimeFormatter df = DateTimeFormatter.ofPattern("(yyyy-MM-dd)");
            doc = Jsoup.connect(URL).get();
            Elements tables = doc.getElementsByTag("table");
            Element content = tables.get(tables.size() - 1);
            Elements trs = content.getElementsByTag("tr");
            Element title = trs.get(0);
            Elements titleTds = title.getElementsByTag("td");
            LocalDate date = LocalDate.parse(titleTds.get(2).text().split(" ")[1], df);
            LocalDate dateWeek = LocalDate.parse(titleTds.get(3).text().split(" ")[1], df);
            LocalDate dateMonth = LocalDate.parse(titleTds.get(4).text().split(" ")[1], df);
            String type = "";
            for (int i = 1; i < trs.size(); i++) {
                try {
                    Element tr = trs.get(i);
                    Elements tds = tr.getElementsByTag("td");
                    if (tds.size() == 1) {
                        type = tds.get(0).getElementsByTag("b").get(0).text();
                    } else if (tds.size() == 5) {
                        Element a = tds.get(0).child(0);
                        String name = a.text();
                        String identifierStr = a.attr("href");
                        String identifier = identifierStr.split("-")[1].split("\\.")[0];
                        String priceStr = tds.get(2).text();
                        String priceBeforeWeekStr = tds.get(3).text();
                        String priceBeforeMonthStr = tds.get(4).text();

                        Material material = Material.builder().date(date).dateBeforeMonth(dateMonth).dateBeforeWeek(dateWeek)
                                .name(name).identifier(identifier).spec(tds.get(1).text())
                                .type(type)
                                .price(StringUtils.isEmpty(priceStr) ? null : new BigDecimal(priceStr))
                                .priceBeforeWeek(calRate(priceStr, priceBeforeWeekStr))
                                .priceBeforeMonth(calRate(priceStr, priceBeforeMonthStr)).build();

                        materialService.save(material);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            log.warn(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 从生意社获取数据失败");
            e.printStackTrace();
        }
    }


    public BigDecimal calRate(String a, String b) {
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            return null;
        }
        BigDecimal aa = new BigDecimal(a);
        BigDecimal bb = new BigDecimal(b);
        return (aa.subtract(bb)).divide(bb, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
    }
}

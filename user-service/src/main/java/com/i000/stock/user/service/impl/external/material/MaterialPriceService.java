package com.i000.stock.user.service.impl.external.material;

import com.i000.stock.user.dao.model.IndustryChain;
import com.i000.stock.user.dao.model.Material;
import com.i000.stock.user.dao.model.MaterialCompany;
import com.i000.stock.user.service.impl.IndustryChainService;
import com.i000.stock.user.service.impl.MaterialCompanyService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 原材料的价格获取服务
 */
@Slf4j
@Service
public class MaterialPriceService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private IndustryChainService industryChainService;

    @Autowired
    private MaterialCompanyService materialCompanyService;

    private static final String PRICE_URL = "http://www.100ppi.com/monitor/";

    public void savePrice() {
        try {
            Document doc;
            DateTimeFormatter df = DateTimeFormatter.ofPattern("(yyyy-MM-dd)");
            doc = Jsoup.connect(PRICE_URL).get();
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


    private static final String ICHAIN_URL = "http://www.100ppi.com/cindex/ichain-397.html";

    public void saveIChain() {
        try {
            Document document = Jsoup.connect(ICHAIN_URL).get();
            Elements myList = document.getElementsByClass("m-list1");
            for (Element element : myList) {
                Elements dt = element.getElementsByTag("dt");
                if (Objects.nonNull(dt) && dt.get(0).text().contains("产业链")) {
                    Elements as = element.getElementsByTag("a");
                    for (Element aElement : as) {
                        IndustryChain industryChain = IndustryChain.builder()
                                .name(aElement.text().split("产业链")[0])
                                .url(aElement.attr("href").split("-")[1].split("\\.")[0]).build();
                        industryChainService.save(industryChain);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String MATERIAL_COMPANY_URL = "http://stock.100ppi.com/ssgs.html";

    public void saveMaterialCompany() {
        try {
            Document document = Jsoup.connect(MATERIAL_COMPANY_URL).get();
            Elements uls = document.getElementsByClass("b-line");
            for (Element element : uls) {
                String name = element.getElementsByClass("cbc1").text();
                List<String> companyCode = getNameCode(element);
                MaterialCompany materialCompany = MaterialCompany.builder()
                        .companyName(companyCode.get(0)).companyCode(companyCode.get(1))
                        .downstreamName(companyCode.get(2)).downstreamCode(companyCode.get(3)).name(name).build();
                materialCompanyService.save(materialCompany);
            }
            System.out.println(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getNameCode(Element element) {
        List<String> result = new ArrayList<>();
        Elements cbc3 = element.getElementsByClass("cbc3");
        for (Element temp : cbc3) {
            Elements as = temp.getElementsByTag("a");
            String code = "";
            String name = "";
            for (Element a : as) {
                code = code + a.attr("href").split("-")[1].split("\\.")[0] + ",";
                name = name + a.text() + ",";
            }
            result.add(name);
            result.add(code);
        }
        return result;
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

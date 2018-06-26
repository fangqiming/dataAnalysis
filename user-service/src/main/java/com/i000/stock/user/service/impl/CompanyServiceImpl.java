package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.CompanyService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:04 2018/4/25
 * @Modified By:
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {


    @Override
    public List<String> getCode() throws IOException {
        List<String> result = new ArrayList<>(4000);
        Document doc = Jsoup.connect("http://quote.eastmoney.com/stocklist.html").get();
        Elements sltit = doc.getElementsByClass("sltit");
        for (Element element : sltit) {
            String prefix = element.child(0).attr("name");
            Element companys = element.nextElementSibling();
            Elements ul = companys.children();
            for (Element li : ul) {
                String info = li.text();
                String code = getCode(info);
                if (code.startsWith("60") || code.startsWith("000") || code.startsWith("002") || code.startsWith("300") || code.startsWith("001")) {
                    result.add(code);
                }
            }
        }
        return result;
    }


    private String getName(String str) {
        if (!StringUtils.isEmpty(str)) {
            try {
                return str.split("\\(")[0];
            } catch (Exception e) {
            }
        }
        return "";
    }

    private String getCode(String str) {
        if (!StringUtils.isEmpty(str)) {
            try {
                return str.split("\\(")[1].split("\\)")[0];
            } catch (Exception e) {
            }
        }
        return "";
    }

}

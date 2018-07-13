package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.KVBo;
import com.i000.stock.user.api.service.external.CompanyInfoCrawlerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:01 2018/7/4
 * @Modified By:
 */
@Service
public class CompanyInfoCrawlerServiceImpl implements CompanyInfoCrawlerService {

    private static final String URL = "http://stockpage.10jqka.com.cn/";

    private static final String CLASS_VALUE = "company_details";

    @Override
    public List<KVBo> getInfo(String code) {
        List<KVBo> kvBos = CompanyInfoCrawlerService.CACHE.get(code);
        if (Objects.isNull(kvBos)) {
            return putCache(code);
        }
        return kvBos;

    }


    @Override
    public List<KVBo> putCache(String code) {
        Document doc = getDocument(code);
        List<KVBo> result = new ArrayList<>(16);
        Elements companyDetail = doc.getElementsByClass(CLASS_VALUE);
        for (Element element : companyDetail) {
            Elements ks = element.getElementsByTag("dt");
            Elements vs = element.getElementsByTag("dd");
            for (int i = 0; i < ks.size(); i++) {
                if (i < 2) {
                    result.add(KVBo.builder()
                            .k(ks.eq(i).text())
                            .v(vs.eq(i).text()).build());
                } else {
                    result.add(KVBo.builder()
                            .k(ks.eq(i).text())
                            .v(vs.eq(i + 1).text()).build());
                }

            }
        }
        //加入缓存
        CACHE.put(code, result);
        return result;
    }


    private Document getDocument(String code) {
        Document doc = null;
        for (int i = 0; i < 5; i++) {
            try {
                doc = Jsoup.connect(URL + code).get();
                if (!Objects.isNull(doc)) {
                    break;
                }
            } catch (Exception e) {

            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }
}

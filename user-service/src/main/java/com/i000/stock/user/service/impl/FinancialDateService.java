package com.i000.stock.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.i000.stock.user.core.util.TimeUtil;
import com.i000.stock.user.dao.mapper.FinancialDateMapper;
import com.i000.stock.user.dao.model.FinancialDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialDateService {


    private static String URL = "https://finance.yahoo.com/calendar/earnings?from=%s&to=%s&day=%s&offset=%s&size=%s";
    private static String FLAG = "root.App.main";

    @Autowired
    private FinancialDateMapper financialDateMapper;

    /**
     * 从雅虎财经爬取财报预公布日期,并保存到数据库中
     *
     * @throws Exception
     */
    public void save() throws Exception {
        financialDateMapper.truncate();
        LocalDate start = LocalDate.now().minusDays(1L);
        LocalDate start1 = LocalDate.now().minusDays(1L);
        LocalDate end = LocalDate.now().plusDays(2L);
        //四层循环,可能会出问题
        while (start.compareTo(end) <= 0) {
            Integer offset = 0;
            while (offset % 100 == 0) {
                String url = String.format(URL, start1.format(TimeUtil.DF),
                        end.format(TimeUtil.DF), start.format(TimeUtil.DF), offset, 100);
                Document document = Jsoup.connect(url).get();
                String text = document.outerHtml();
                String[] lines = text.split("\n");
                for (String line : lines) {
                    if (line.startsWith(FLAG)) {
                        JSONArray array = getArray(line);
                        offset = offset + array.size();
                        save(array);
                        break;
                    }
                }
                if (offset == 0) {
                    //防止出现死循环
                    break;
                }

            }
            start = start.plusDays(1L);
            Thread.sleep(5000L);
        }

    }

    /**
     * 获取指定股票的预测美股收益
     *
     * @param symbol
     * @return
     */
    public String getEps(String symbol) {
        EntityWrapper<FinancialDate> ew = new EntityWrapper();
        ew.where("symbol={0}", symbol);
        List<FinancialDate> financialDates = financialDateMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(financialDates)) {
            return StringUtils.isEmpty(financialDates.get(0).getEps())
                    ? "未知" : financialDates.get(0).getEps();
        }
        return null;
    }

    /**
     * 从JSON对象中获取到财报对象
     *
     * @param line
     * @return
     */
    private JSONArray getArray(String line) {
        try {

            String s2 = line.split("main = ")[1];
            JSONObject json = JSONObject.parseObject(s2.substring(0, s2.length() - 1));

            JSONArray array = json.getJSONObject("context").getJSONObject("dispatcher").getJSONObject("stores")
                    .getJSONObject("ScreenerResultsStore").getJSONObject("results").getJSONArray("rows");
            return array;

        } catch (Exception e) {

        }
        return new JSONArray(0);
    }

    /**
     * 将财报的JSON对象转存到数据库
     *
     * @param array
     */
    private void save(JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject json = array.getJSONObject(i);
            String symbol = json.getString("ticker");
            System.out.println(json.getString("startdatetime"));
            String date = json.getString("startdatetime").substring(0, 10);
            String eps = json.getString("epsestimate");
            FinancialDate financialDate = FinancialDate.builder().date(date).eps(eps).symbol(symbol).build();
            financialDateMapper.insert(financialDate);
        }
    }
}

package com.i000.stock.user.service.impl.parse;

import com.i000.stock.user.api.service.original.ParseService;
import com.i000.stock.user.dao.mapper.TradeMapper;
import com.i000.stock.user.dao.model.Trade;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:37 2018/4/27
 * @Modified By:
 */
@Component
public class ParseToTrade implements ParseService {

    @Resource
    private TradeMapper tradeMapper;

    private String start = "------ Todays Trades ------";

    public boolean needSave(String part) {
        List<String> parts = fetchLines(part);
        LocalDate maxDate = tradeMapper.getMaxDate();

        if (CollectionUtils.isEmpty(parts)) {
            //如果时空就保存
            LocalDate nowDate = LocalDate.parse(part.split("trade_desc_")[1].split("\\.txt")[0],
                    DateTimeFormatter.ofPattern("yyyyMMdd"));
            if (Objects.isNull(maxDate) || maxDate.compareTo(nowDate) < 0) {
                tradeMapper.insert(Trade.builder().date(nowDate).build());
            }
        }
        if (Objects.isNull(maxDate)) {
            //如果为空也应该记录下来
            return true;
        }
        for (String line : parts) {
            LocalDate parse = LocalDate.parse(line.substring(0, 10), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            if (parse.compareTo(maxDate) > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public LocalDate save(String original) {
        String part = getPart(original);
        LocalDate parse = null;
        if (needSave(part)) {
            List<String> parts = fetchLines(part);
            for (String line : parts) {
                String[] split = line.split("\t");
                parse = LocalDate.parse(split[0], DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                tradeMapper.insert(Trade.builder().date(parse)
                        .type(split[1])
                        .name(split[2])
                        .price(new BigDecimal(split[4]))
                        .action(split[3])
                        .note(split[7]).build());
            }
        }
        return parse;
    }


    public String getPart(String original) {
        String[] split = original.split(SECTION.pattern());
        for (String part : split) {
            if (part.startsWith(start)) {
                return part;
            }
        }
        return "";
    }

    private List<String> fetchLines(String original) {

        List<String> result = new ArrayList<>();
        String[] split = original.split(LINE.pattern());
        for (String line : split) {
            if (DATE.matcher(line).find()) {
                result.add(line.substring(0, line.length() - 1));
            }
        }
        return result;
    }
}

package com.i000.stock.user.service.impl.parse;

import com.i000.stock.user.api.service.original.ParseService;
import com.i000.stock.user.dao.mapper.HoldMapper;
import com.i000.stock.user.dao.model.Hold;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:37 2018/4/27
 * @Modified By:
 */
@Component
public class ParseToHold implements ParseService {

    private String start = "------ Portfolio for 20";

    @Resource
    private HoldMapper holdMapper;


    @Override
    public LocalDate save(String original) {
        List<String> parts = getPart(original);
        LocalDate maxDate = holdMapper.getMaxDate();
        LocalDate parse = null;
        LocalDate today = getData(parts.get(0));
        Map<String, List<String>> stringListMap = fetchLines(parts);
        boolean hasData = false;
        for (String key : stringListMap.keySet()) {
            for (String line : stringListMap.get(key)) {
                String[] split = line.split("\t");
                parse = LocalDate.parse(split[5], DateTimeFormatter.ofPattern("yyyyMMdd"));
                if (Objects.isNull(maxDate) || parse.compareTo(maxDate) > 0) {
                    hasData = true;
                    holdMapper.insert(Hold.builder().gain(new BigDecimal(split[8]).divide(new BigDecimal(100), 5, BigDecimal.ROUND_HALF_UP))
                            .holdDay(Integer.valueOf(split[7]))
                            .name(split[0])
                            .newDate(parse)
                            .newPrice(new BigDecimal(split[4]))
                            .oldDate(LocalDate.parse(split[2], DateTimeFormatter.ofPattern("yyyyMMdd")))
                            .oldPrice(new BigDecimal(split[1]))
                            .newRank(new BigDecimal(split[3]))
                            .oldRank(new BigDecimal(split[6]))
                            .type(key).build());
                }
            }
        }
        //如果没有就保存空的数据到数据库中，当做当前的持股
        if (!hasData) {
            holdMapper.insert(Hold.builder().newDate(today).build());
        }

        return parse;
    }

    private LocalDate getData(String info) {
        String date = info.split("for ")[1].split(" , ")[0];
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyyMMdd"));
    }


    public List<String> getPart(String original) {
        List<String> result = new ArrayList<>();
        String[] split = original.split(SECTION.pattern());
        for (String part : split) {
            if (part.startsWith(start)) {
                result.add(part);
            }
        }
        return result;
    }

    private Map<String, List<String>> fetchLines(List<String> parts) {
        Map<String, List<String>> result = new HashMap<>(4);
        for (String part : parts) {
            String[] split = part.split(LINE.pattern());
            String type = "";
            List<String> lines = new ArrayList<>();
            for (String line : split) {
                if (VALID_ITEM.matcher(line).find()) {
                    lines.add(line.substring(0, line.length() - 1));
                }
                if (line.startsWith("------ Portfolio")) {
                    type = line.split(", ")[1].split(" ")[0];
                }
            }
            result.put(type, lines);
        }
        return result;
    }

}

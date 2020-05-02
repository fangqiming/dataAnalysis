package com.i000.stock.user.service.impl.parse;

import com.i000.stock.user.api.service.original.ParseService;
import com.i000.stock.user.dao.mapper.LineMapper;
import com.i000.stock.user.dao.model.Line;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:35 2018/4/27
 * @Modified By:
 */
@Component
public class ParseToLine implements ParseService {

    @Resource
    private LineMapper lineMapper;

    private String start = "------ Daily Snapshot ------";


    public boolean needSave(String part) {
        LocalDate maxDate = lineMapper.getMaxDate();
        if (Objects.isNull(maxDate)) {
            return true;
        }
        String lineMax = "";
        for (String line : fetchLines(part)) {
            lineMax = line.substring(0, 10);
        }
        LocalDate parse = LocalDate.parse(lineMax, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return parse.compareTo(maxDate) > 0;
    }

    @Override
    public LocalDate save(String original,LocalDate date) {
        String ownPart = getPart(original);
        LocalDate maxDate = lineMapper.getMaxDate();
        LocalDate parse = null;
        if (needSave(ownPart)) {
            for (String line : fetchLines(ownPart)) {
                String[] split = line.split("\t");
                parse = LocalDate.parse(split[0], DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                if (Objects.isNull(maxDate) || parse.compareTo(maxDate) > 0) {
                    lineMapper.insert(Line.builder()
                            .date(parse)
                            .gain(new BigDecimal(split[7]).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP))
                            .aiMarket(new BigDecimal(split[6]))
                            .baseMarket(new BigDecimal(split[5])).build());
                }
            }
        }
        return parse;
    }


    public String getPart(String original) {
        String[] split = original.split(SECTION.pattern());
        for (String part : split) {
            if (part.contains(start)) {
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

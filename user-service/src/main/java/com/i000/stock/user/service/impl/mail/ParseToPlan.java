package com.i000.stock.user.service.impl.mail;

import com.i000.stock.user.api.service.MailParseService;
import com.i000.stock.user.dao.mapper.PlanMapper;
import com.i000.stock.user.dao.model.Plan;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:36 2018/4/27
 * @Modified By:
 */
@Component
public class ParseToPlan implements MailParseService {

    @Resource
    private PlanMapper planMapper;

    private String start = "------ Tomorrows Plan ------";


    public boolean needSave(String part) {
        LocalDate maxDate = planMapper.getMaxDate();
        if (Objects.isNull(maxDate)) {
            return true;
        }
        List<String> parts = fetchLines(part);
        if (!CollectionUtils.isEmpty(parts)) {
            for (String line : parts) {
                LocalDate parse = LocalDate.parse(line.substring(0, 10), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                if (parse.compareTo(maxDate) > 0) {
                    return true;
                }
            }
        } else {
            //如果当前的推荐信息为空,就保存空数据的数据库中
            LocalDate nowDate = LocalDate.parse(part.split("plan_desc_")[1].split("\\.txt")[0],
                    DateTimeFormatter.ofPattern("yyyyMMdd"));
            if (maxDate.compareTo(nowDate) < 0) {
                planMapper.insert(Plan.builder().newDate(nowDate).build());
            }
        }

        return false;
    }

    @Override
    public LocalDate save(String original) {
        String part = getPart(original);
        LocalDate parse = null;
        if (needSave(part)) {
            for (String line : fetchLines(part)) {
                String[] split = line.split("\t");
                parse = LocalDate.parse(split[0], DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                planMapper.insert(Plan.builder().action(split[3])
                        .name(split[2])
                        .newDate(parse)
                        .note(split[8])
                        .rank(new BigDecimal(split[7]))
                        .type(split[1]).build());
            }
        }
        return parse;
    }


    public String getPart(String original) {
        String[] split = original.split(section.pattern());
        for (String part : split) {
            if (part.startsWith(start)) {
                return part;
            }
        }
        return "";
    }

    private List<String> fetchLines(String original) {

        List<String> result = new ArrayList<>();
        String[] split = original.split(line.pattern());
        for (String line : split) {
            if (date.matcher(line).find()) {
                result.add(line.substring(0, line.length() - 1));
            }
        }
        return result;
    }

}

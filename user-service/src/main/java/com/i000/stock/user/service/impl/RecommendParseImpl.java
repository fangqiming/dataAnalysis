package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.MailParseService;
import com.i000.stock.user.service.impl.mail.ParseToHold;
import com.i000.stock.user.service.impl.mail.ParseToLine;
import com.i000.stock.user.service.impl.mail.ParseToPlan;
import com.i000.stock.user.service.impl.mail.ParseToTrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:06 2018/5/8
 * @Modified By:
 */
@Slf4j
@Component
public class RecommendParseImpl {

    @Autowired
    private ParseToHold parseToHold;
    @Autowired
    private ParseToLine parseToLine;
    @Autowired
    private ParseToPlan parseToPlan;
    @Autowired
    private ParseToTrade parseToTrade;

    private List<MailParseService> parseServiceList;

    @PostConstruct
    public void init() {
        parseServiceList = Arrays.asList(parseToHold, parseToLine, parseToPlan, parseToTrade);
    }


    public LocalDate parse(String content) {
        log.debug(new Date() + "得到了推送的推荐信息");
        List<LocalDate> result = new ArrayList<>(4);
        for (MailParseService mailParseService : parseServiceList) {
            result.add(mailParseService.save(content));
        }
        List<LocalDate> collect = result.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return CollectionUtils.isEmpty(collect) ? null : collect.get(0);
    }

}
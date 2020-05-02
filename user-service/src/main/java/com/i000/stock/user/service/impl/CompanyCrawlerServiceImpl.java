package com.i000.stock.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.i000.stock.user.api.entity.bo.*;
import com.i000.stock.user.api.entity.constant.PeriodEnum;
import com.i000.stock.user.api.service.external.CompanyCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author:qmfang
 * @Description: 公司信息
 * @Date:Created in 15:04 2018/4/25
 * @Modified By:
 */
@Service
@Transactional
public class CompanyCrawlerServiceImpl implements CompanyCrawlerService {

    private static final String JQ_URL = "https://dataapi.joinquant.com/apis";
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<String> getCode() throws IOException {
        Map<String, String> codeName = getCodeName();
        return new ArrayList<>(codeName.keySet());
    }


    @Override
    public Map<String, String> getCodeName() {
        String body = getAllCode();
        Map<String, String> result = new HashMap<>(4000);
        String[] item = body.split("\n");
        for (String str : item) {
            if (!StringUtils.isEmpty(str)) {
                if (!str.contains("code")) {
                    String[] split = str.split(",");
                    String code = split[0].split("\\.")[0];
                    String name = split[1];
                    result.put(code, name);
                }
            }
        }
        return result;
    }

    @Override
    public List<JQKlineBo> findKLine(String code, PeriodEnum periodEnum, Integer count) {
        code = getCode(code);
        List<JQKlineBo> result = new ArrayList<>(count);
        String date = LocalDate.now().format(DF);
        JSONObject param = new JSONObject();
        param.put("method", "get_price");
        param.put("token", getToken());
        param.put("code", code);
        param.put("count", count);
        param.put("unit", periodEnum.getValue());
        param.put("end_date", date);
        param.put("fq_ref_date", date);
        String body = getRequest(param);
        String[] item = body.split("\n");
        for (String str : item) {
            if (!StringUtils.isEmpty(str)) {
                if (!str.contains("close")) {
                    String[] split = str.split(",");
                    JQKlineBo jqKlineBo = JQKlineBo.builder().date(split[0]).open(Double.valueOf(split[1]))
                            .close(Double.valueOf(split[2])).high(Double.valueOf(split[3])).low(Double.valueOf(split[4]))
                            .volume(Double.valueOf(split[5])).money(Double.valueOf(split[6]))
                            .preClose(Double.valueOf(split[11])).code(code.split("\\.")[0])
                            .build();
                    result.add(jqKlineBo);
                }
            }
        }
        return result;
    }

    @Override
    public Price getPrice(String code) {
        List<JQKlineBo> kLine = findKLine(code, PeriodEnum.DAY_1, 1);
        if (!CollectionUtils.isEmpty(kLine)) {
            JQKlineBo jqKlineBo = kLine.get(0);
            return Price.builder()
                    .amount(BigDecimal.valueOf(jqKlineBo.getMoney()))
                    .close(BigDecimal.valueOf(jqKlineBo.getPreClose()))
                    .price(BigDecimal.valueOf(jqKlineBo.getClose()))
                    .date(jqKlineBo.getDate())
                    .high(BigDecimal.valueOf(jqKlineBo.getHigh()))
                    .low(BigDecimal.valueOf(jqKlineBo.getLow()))
                    .volume(BigDecimal.valueOf(jqKlineBo.getVolume()))
                    .code(jqKlineBo.getCode())
                    .open(BigDecimal.valueOf(jqKlineBo.getOpen())).build();
        }
        return null;

    }

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenBo tokenBo;

    private static TokenCache TOKENCACHE;

    public String getToken() {
        if (Objects.isNull(TOKENCACHE) || StringUtils.isEmpty(TOKENCACHE.getToken())
                || TOKENCACHE.getTime().getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
            String token = getTokenFromNet();
            TOKENCACHE = TokenCache.builder().time(LocalDateTime.now()).token(token).build();
            return token;
        }
        return TOKENCACHE.getToken();
    }

    private String getTokenFromNet() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(tokenBo, headers);
        ResponseEntity<String> request = restTemplate.postForEntity(JQ_URL, httpEntity, String.class);
        String body = request.getBody();
        return body;
    }

    public String getAllCode() {
        String token = getToken();
        String date = LocalDate.now().format(DF);
        AllCodeParamBO param = AllCodeParamBO.builder().code("stock").date(date)
                .method("get_all_securities").token(token).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(param, headers);
        ResponseEntity<String> request = restTemplate.postForEntity(JQ_URL, httpEntity, String.class);
        String body = request.getBody();
        return body;
    }

    private String getCode(String code) {
        if (!StringUtils.isEmpty(code)) {
            if (code.contains(".")) {
                return code;
            } else {
                code = code.startsWith("6") ? code + ".XSHG" : code + ".XSHE";
            }
        }
        return code;
    }

    private String getRequest(JSONObject param) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(param, headers);
        ResponseEntity<String> request = restTemplate.postForEntity(JQ_URL, httpEntity, String.class);
        String body = request.getBody();
        return body;
    }

}

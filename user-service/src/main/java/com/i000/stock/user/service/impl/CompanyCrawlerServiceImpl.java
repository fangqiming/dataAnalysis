package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.AllCodeParamBO;
import com.i000.stock.user.api.entity.bo.TokenBo;
import com.i000.stock.user.api.entity.bo.TokenCache;
import com.i000.stock.user.api.service.external.CompanyCrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
        String url = "https://dataapi.joinquant.com/apis";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(tokenBo, headers);
        ResponseEntity<String> request = restTemplate.postForEntity(url, httpEntity, String.class);
        String body = request.getBody();
        return body;
    }

    private String getAllCode() {
        String token = getToken();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        AllCodeParamBO param = AllCodeParamBO.builder().code("stock").date(date)
                .method("get_all_securities").token(token).build();
        String url = "https://dataapi.joinquant.com/apis";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity httpEntity = new HttpEntity(param, headers);
        ResponseEntity<String> request = restTemplate.postForEntity(url, httpEntity, String.class);
        String body = request.getBody();
        return body;
    }
}

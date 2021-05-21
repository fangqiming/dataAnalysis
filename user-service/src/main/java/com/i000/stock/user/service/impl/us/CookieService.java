package com.i000.stock.user.service.impl.us;

import com.i000.stock.user.api.entity.bo.Cache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 专门用于提供需求的Cookie
 * 注意由于缓存的关系，不同网站不能同时使用
 */
@Slf4j
@Service
public class CookieService {

    private Cache<HttpEntity<MultiValueMap<String, String>>> COOKIE_CACHE;

    public HttpEntity<MultiValueMap<String, String>> getCookie(String url) {
        if (Objects.isNull(COOKIE_CACHE) || diff10Min(COOKIE_CACHE.getTime())) {
            HttpEntity<MultiValueMap<String, String>> result = getXqHttpEntity(url);
            COOKIE_CACHE = new Cache<>();
            COOKIE_CACHE.setTime(LocalDateTime.now());
            COOKIE_CACHE.setData(result);
        }
        return COOKIE_CACHE.getData();
    }

    private Boolean diff10Min(LocalDateTime before) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(before, now);
        long minutes = duration.toMinutes();
        return minutes > 10;
    }

    private HttpEntity<MultiValueMap<String, String>> getXqHttpEntity(String url) {
        try {
            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(url);
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            int statusCode = httpClient.executeMethod(getMethod);
            Cookie[] cookies = httpClient.getState().getCookies();
            List<String> cookieList = new ArrayList<>();
            for (Cookie c : cookies) {
                cookieList.add(c.toString());
            }
            HttpHeaders headers = new HttpHeaders();
            headers.put(HttpHeaders.COOKIE, cookieList);

            MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
            return new HttpEntity<>(param, headers);
        } catch (Exception e) {
            log.warn("雪球的cookie获取失败");
        }
        return null;
    }
}

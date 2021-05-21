package com.i000.stock.user.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.i000.stock.user.api.entity.vo.TopicVo;
import com.i000.stock.user.api.service.discuss.TopicService;
import com.i000.stock.user.core.context.RequestContext;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.Topic;
import com.i000.stock.user.service.impl.us.CookieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:08 2018/4/24
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/topic")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TopicController {

    @Resource
    private TopicService topicService;

    @Resource
    private CookieService cookieService;

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/roe")
    public Object get_roe(@RequestParam String code) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/finance/us/indicator.json?symbol=%s&type=Q4&is_detail=true&count=5&timestamp=", code);
        HttpEntity<MultiValueMap<String, String>> cookie = cookieService.getCookie("https://xueqiu.com/");
        ResponseEntity<JSONObject> stock = restTemplate.exchange(url, HttpMethod.GET, cookie, JSONObject.class);
        try {
            return stock.getBody().getJSONObject("data").getJSONArray("list").getJSONObject(0).getJSONArray("roe_avg").get(0);
        } catch (Exception e) {
            return "Null";
        }
    }


    @GetMapping("/index")
    public Object get_pe(@RequestParam String code, @RequestParam String name) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/quote.json?symbol=%s&extend=detail", code);
        HttpEntity<MultiValueMap<String, String>> cookie = cookieService.getCookie("https://xueqiu.com/");
        ResponseEntity<JSONObject> stock = restTemplate.exchange(url, HttpMethod.GET, cookie, JSONObject.class);
        try {
            return stock.getBody().getJSONObject("data").getJSONObject("quote").getString(name);
        } catch (Exception e) {
            return "Null";
        }
    }

    @GetMapping("/cash")
    public Object get_pe(@RequestParam String code) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/finance/us/cash_flow.json?symbol=%s&type=Q4&is_detail=true&count=5&timestamp=", code);
        HttpEntity<MultiValueMap<String, String>> cookie = cookieService.getCookie("https://xueqiu.com/");
        ResponseEntity<JSONObject> stock = restTemplate.exchange(url, HttpMethod.GET, cookie, JSONObject.class);
        try {
            return stock.getBody().getJSONObject("data").getJSONArray("list").getJSONObject(0).getJSONArray("net_cash_provided_by_oa").get(0);
        } catch (Exception e) {
            return "Null";
        }
    }

    @GetMapping("/growth_y")
    public Object get_growth_y(@RequestParam String code, @RequestParam String name) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/finance/us/income.json?symbol=%s&type=Q4&is_detail=true&count=5&timestamp=", code);
        HttpEntity<MultiValueMap<String, String>> cookie = cookieService.getCookie("https://xueqiu.com/");
        ResponseEntity<JSONObject> stock = restTemplate.exchange(url, HttpMethod.GET, cookie, JSONObject.class);
        try {
            return stock.getBody().getJSONObject("data").getJSONArray("list").getJSONObject(0)
                    .getJSONArray(name).get(1);
        } catch (Exception e) {
            return "Null";
        }
    }

    @GetMapping("/value_y")
    public Object value_y(@RequestParam String code, @RequestParam String name) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/finance/us/income.json?symbol=%s&type=Q4&is_detail=true&count=5&timestamp=", code);
        HttpEntity<MultiValueMap<String, String>> cookie = cookieService.getCookie("https://xueqiu.com/");
        ResponseEntity<JSONObject> stock = restTemplate.exchange(url, HttpMethod.GET, cookie, JSONObject.class);
        try {
            return stock.getBody().getJSONObject("data").getJSONArray("list").getJSONObject(0)
                    .getJSONArray(name).get(0);
        } catch (Exception e) {
            return "Null";
        }
    }

    @GetMapping("/growth_q")
    public Object get_growth_q(@RequestParam String code, @RequestParam String name) {
        String url = String.format("https://stock.xueqiu.com/v5/stock/finance/us/income.json?symbol=%s&type=all&is_detail=true&count=5&timestamp=", code);
        HttpEntity<MultiValueMap<String, String>> cookie = cookieService.getCookie("https://xueqiu.com/");
        ResponseEntity<JSONObject> stock = restTemplate.exchange(url, HttpMethod.GET, cookie, JSONObject.class);
        try {
            JSONObject jsonObject = stock.getBody().getJSONObject("data").getJSONArray("list").getJSONObject(0);
            if (jsonObject.getString("report_name").contains("季")) {
                return jsonObject.getJSONArray(name).get(1);
            } else {
                return stock.getBody().getJSONObject("data").getJSONArray("list").getJSONObject(1).getJSONArray(name).get(1);
            }
        } catch (Exception e) {
            return "Null";
        }
    }

    //
    //

    /**
     * 127.0.0.1:8082/topic/create
     *
     * @param topicVo
     * @return
     */
    @PostMapping("/create")
    public ResultEntity create(@RequestBody TopicVo topicVo) {
        String userCode = RequestContext.getInstance().getAccountCode();
        topicVo.setUserCode(userCode);
        ValidationUtils.validate(topicVo);
        Topic topic = ConvertUtils.beanConvert(topicVo, new Topic());
        topic.setClickNum(0);
        topic.setCreatedTime(LocalDateTime.now());
        Long topicId = topicService.create(topic);
        return Results.newNormalResultEntity("id", topicId);
    }

    /**
     * 127.0.0.1:8082/topic/search
     *
     * @param baseSearchVo
     * @return
     */
    @GetMapping("/search")
    public ResultEntity search(BaseSearchVo baseSearchVo) {
        ValidationUtils.validate(baseSearchVo);
        PageResult<Topic> pageResultInfo = topicService.search(baseSearchVo);
        return CollectionUtils.isEmpty(pageResultInfo.getList())
                ? Results.newPageResultEntity(0L, new ArrayList<>(0)) :
                Results.newPageResultEntity(pageResultInfo.getTotal(), ConvertUtils.listConvert(pageResultInfo.getList(),
                        TopicVo.class, (t, m) -> t.setUserCode(getUserName(m.getUserCode()))));
    }

    /**
     * 127.0.0.1:8082/topic/get
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public ResultEntity get(@RequestParam Long id) {
        ValidationUtils.validateId(id, "话题主键不合法");
        Topic topic = topicService.get(id);
        return Results.newSingleResultEntity(ConvertUtils.beanConvert(topic, new TopicVo(), (t, m) -> {
            t.setUserCode(getUserName(m.getUserCode()));
        }));
    }

    private String getUserName(String userName) {
        return "echo_gou".equals(userName) ? "匿名用户" : userName;
    }

}

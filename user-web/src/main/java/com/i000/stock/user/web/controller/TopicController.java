package com.i000.stock.user.web.controller;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

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
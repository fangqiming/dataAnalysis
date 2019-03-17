package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.discuss.TopicService;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.TopicMapper;
import com.i000.stock.user.dao.model.Topic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:47 2018/4/24
 * @Modified By:
 */
@Service
@Transactional
public class TopicServiceImpl implements TopicService {

    @Resource
    private TopicMapper topicMapper;

    @Override
    public Long create(Topic topic) {
        topicMapper.insert(topic);
        return topic.getId();
    }

    @Override
    public PageResult<Topic> search(BaseSearchVo search) {
        search.setStart();
        List<Topic> topicList = topicMapper.search(search);
        Long count = topicMapper.count();
        PageResult<Topic> result = new PageResult<>();
        result.setList(topicList);
        result.setTotal(count);
        return result;
    }


    @Override
    public Topic get(Long id) {
        return topicMapper.selectById(id);
    }

    @Override
    public String getUserCode(Long id) {
        return topicMapper.getUserCode(id);
    }
}

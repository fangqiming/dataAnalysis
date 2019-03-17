package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.vo.ReplyVo;
import com.i000.stock.user.api.entity.vo.ReplyVos;
import com.i000.stock.user.api.service.discuss.ReplyService;
import com.i000.stock.user.core.util.ConvertUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.mapper.ReplyMapper;
import com.i000.stock.user.dao.mapper.TopicMapper;
import com.i000.stock.user.dao.model.Reply;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:45 2018/4/24
 * @Modified By:
 */
@Service
@Transactional
public class ReplyServiceImpl implements ReplyService {

    @Resource
    private ReplyMapper replyMapper;

    @Resource
    private TopicMapper topicMapper;

    @Override
    public Reply create(Reply reply) {
        replyMapper.insert(reply);
        return reply;
    }

    @Override
    public PageResult<ReplyVos> search(BaseSearchVo baseSearchVo, Long id) {
        baseSearchVo.setStart();
        List<Reply> replies = replyMapper.search(baseSearchVo, id);
        topicMapper.updateNum(id);
        Long count = replyMapper.count();
        PageResult<ReplyVos> result = new PageResult<>();
        if (!CollectionUtils.isEmpty(replies)) {
            List<ReplyVos> list = new ArrayList<>(replies.size());
            for (Reply reply : replies) {
                list.add(findReply(reply));
            }
            result.setList(list);
        }
        result.setTotal(count);
        return result;
    }

    @Override
    public List<Reply> find(Long id) {
        return replyMapper.find(id);
    }


    @Override
    public ReplyVos findReply(Reply reply) {
        ReplyVos result = new ReplyVos();
        result.setReply(ConvertUtils.beanConvert(reply, new ReplyVo()));
        List<Reply> replies = find(reply.getId());
        if (!CollectionUtils.isEmpty(replies)) {
            List<ReplyVos> son = new ArrayList<>();
            for (Reply reply1 : replies) {
                son.add(findReply(reply1));
            }
            result.setSon(son);
        }
        return result;
    }

    @Override
    public String getUserCode(Long id) {
        return replyMapper.getUserCode(id);
    }
}

package com.i000.stock.user.api.service.discuss;


import com.i000.stock.user.api.entity.vo.ReplyVos;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.Reply;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:33 2018/4/24
 * @Modified By:
 */
public interface ReplyService {

    /**
     * 创建评论
     *
     * @param reply
     * @return
     */
    Reply create(Reply reply);

    /**
     * 分页查找回复信息
     *
     * @param baseSearchVo
     * @param id
     * @return
     */
    Page<ReplyVos> search(BaseSearchVo baseSearchVo, Long id);

    /**
     * 根据评论查ID找评论的回复
     *
     * @param id
     * @return
     */
    List<Reply> find(Long id);


    /**
     * 递归查找评论回复
     *
     * @param reply
     * @return
     */
    ReplyVos findReply(Reply reply);

    /**
     * 获取某个主键的用户码
     *
     * @param id
     * @return
     */
    String getUserCode(Long id);
}

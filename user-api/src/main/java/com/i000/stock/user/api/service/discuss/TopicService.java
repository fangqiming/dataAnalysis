package com.i000.stock.user.api.service.discuss;

import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.Topic;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:36 2018/4/24
 * @Modified By:
 */
public interface TopicService {

    /**
     * 创建话题
     *
     * @param topic
     * @return
     */
    Long create(Topic topic);

    /**
     * 分页查询话题
     *
     * @param search
     * @return
     */
    PageResult<Topic> search(BaseSearchVo search);

    /**
     * 根据话题主键获取话题
     *
     * @param id
     * @return
     */
    Topic get(Long id);

    /**
     * 根据话题主键获取用户userCode
     *
     * @param id
     * @return
     */
    String getUserCode(Long id);

}

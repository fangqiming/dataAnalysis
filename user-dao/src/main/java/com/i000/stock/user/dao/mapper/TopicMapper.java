package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.Topic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:35 2018/4/24
 * @Modified By:
 */
public interface TopicMapper extends BaseMapper<Topic> {

    /**
     * 分页查询topic
     *
     * @param baseSearchVo
     * @return
     */
    List<Topic> search(@Param("baseSearchVo") BaseSearchVo baseSearchVo);

    /**
     * 计算分页的数据总条数
     *
     * @return
     */
    Long count();

    @Update("update topic set click_num = click_num+1 where id=${id}")
    void updateNum(@Param("id") Long id);

    @Update("update topic set good_num = good_num+1 where id=${id}")
    void updateGood(@Param("id") Long id);

    @Update("update topic set bad_num = bad_num+1 where id=${id}")
    void updateBad(@Param("id") Long id);

    @Select("select user_code from topic where id=${id}")
    String getUserCode(@Param("id") Long id);
}

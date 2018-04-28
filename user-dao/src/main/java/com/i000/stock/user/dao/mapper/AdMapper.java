package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.Ad;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:14 2018/4/28
 * @Modified By:
 */
public interface AdMapper extends BaseMapper<Ad> {

    /**
     * 根据key获取相应的对象
     *
     * @param key
     * @return
     */
    @Select("select * from `ad` where `key`=#{key}")
    Ad get(@Param("key") String key);

    /**
     * 根据key更新value的值
     *
     * @param ad
     */
    @Update("UPDATE `ad` set value=#{ad.value} where `key`=#{ad.key}")
    void updateByKey(@Param("ad") Ad ad);
}

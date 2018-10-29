package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.model.ReverseRepo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:14 2018/10/26
 * @Modified By:
 */
public interface ReverseRepoMapper extends BaseMapper<ReverseRepo> {

    /**
     * 分页查询逆回购记录
     *
     * @param userCode
     * @param baseSearchVo
     * @return
     */
    List<ReverseRepo> search(@Param("userCode") String userCode, @Param("baseSearchVo") BaseSearchVo baseSearchVo);

    @Select("select found_rows()")
    Long pageTotal();
}

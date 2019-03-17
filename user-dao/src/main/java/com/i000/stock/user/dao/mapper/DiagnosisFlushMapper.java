package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.DiagnosisFlush;
import org.apache.ibatis.annotations.Select;

public interface DiagnosisFlushMapper extends BaseMapper<DiagnosisFlush> {

    @Select("truncate diagnosis_flush")
    void clean();
}

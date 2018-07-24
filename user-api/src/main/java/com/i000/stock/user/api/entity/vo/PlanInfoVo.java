package com.i000.stock.user.api.entity.vo;

import com.i000.stock.user.api.entity.bo.KVBo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:55 2018/7/4
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanInfoVo {

    private List<KVBo> info;
    private String min;
    private String daily;
    private String weekly;
    private String monthly;
    private String select;
    private String showImg;
}

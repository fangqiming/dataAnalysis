package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:37 2018/7/3
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageGainVo {
    private String Title;
    private List<GainVo> gain;
}

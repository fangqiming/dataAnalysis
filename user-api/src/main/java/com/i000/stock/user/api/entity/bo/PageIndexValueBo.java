package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:43 2018/7/3
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageIndexValueBo {
    private BigDecimal szGain;
    private BigDecimal hsGain;
    private BigDecimal cybGain;
}

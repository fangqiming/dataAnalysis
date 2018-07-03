package com.i000.stock.user.api.entity.bo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:25 2018/7/3
 * @Modified By:
 */
@Data
@Builder
public class IndexValueBo {
    private LocalDate date;
    private BigDecimal sz;
    private BigDecimal hs;
    private BigDecimal cyb;
}

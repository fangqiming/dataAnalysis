package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.api.jackson.ActionSerializer;
import com.i000.stock.user.api.jackson.TypeSerializer;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:17 2018/4/27
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanVo {

    private Long id;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate newDate;
    private String name;
    @JsonSerialize(using = TypeSerializer.class)
    private String type;
    @JsonSerialize(using = ActionSerializer.class)
    private String action;
    private BigDecimal rank;
    private String note;

    /**
     * 建议投资比
     */
    private BigDecimal investmentRatio;
    /**
     * 建议投资额
     */
    private BigDecimal amount;

    private String stockName;

    //链接到雪球
    private String url;
}

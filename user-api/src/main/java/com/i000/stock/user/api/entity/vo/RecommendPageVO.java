package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:10 2018/2/23
 * @Modified By:
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecommendPageVO {
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;
    private BigDecimal gainRate;
    private List<PlanVo> recommend;
}

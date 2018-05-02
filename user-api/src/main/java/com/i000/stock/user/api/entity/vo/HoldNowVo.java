package com.i000.stock.user.api.entity.vo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.api.jackson.TypeSerializer;
import com.i000.stock.user.core.jackson.serialize.BigDecimalRoundSerializer;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:57 2018/4/28
 * @Modified By:
 */
@Data
public class HoldNowVo {

    private Long id;
    private String name;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate oldDate;
    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal oldPrice;
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate newDate;
    @JsonSerialize(using = BigDecimalRoundSerializer.class)
    private BigDecimal newPrice;
    private Integer holdDay;
    private BigDecimal gain;
    @JsonSerialize(using = TypeSerializer.class)
    private String type;
    private Integer amount;

    //成本
    private BigDecimal cost;
    //目前价值
    private BigDecimal value;
    //收益绝对值
    private BigDecimal earning;

}

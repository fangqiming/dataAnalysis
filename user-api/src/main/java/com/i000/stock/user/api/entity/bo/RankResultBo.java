package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankResultBo {

    private String code;

    private LocalDate date;

    private BigDecimal score;

    private String name;

    /**
     * 同花顺诊股得分
     */
    private BigDecimal diagnosis;

    /**
     * 总计得分
     */
    private BigDecimal total;
}

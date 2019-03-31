package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActualDisc {

    @TableId
    private Long id;

    private String name;

    private BigDecimal marketCap;

    private BigDecimal netWorth;

    private LocalDate date;

    private String type;

}

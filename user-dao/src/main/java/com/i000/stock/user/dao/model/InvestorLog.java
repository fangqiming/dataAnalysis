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
@NoArgsConstructor
@AllArgsConstructor
public class InvestorLog {

    @TableId
    private Long id;

    private LocalDate date;

    private String name;

    private String bankCard;

    private String type;

    private BigDecimal share;

    private BigDecimal amount;

    private String remark;

}

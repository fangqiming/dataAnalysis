package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cpi {

    @TableId
    private Long id;

    private String date;

    private BigDecimal total;

    private BigDecimal city;

    private BigDecimal countryside;
}
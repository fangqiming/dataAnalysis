package com.i000.stock.user.dao.model;

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
public class IndexUs {

    private Long id;

    private LocalDate date;

    private BigDecimal dji;

    private BigDecimal nasdaq;

    private BigDecimal sp500;
}

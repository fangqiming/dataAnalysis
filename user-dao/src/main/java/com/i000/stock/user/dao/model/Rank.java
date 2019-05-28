package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Rank {

    private String code;

    private LocalDate date;

    private BigDecimal score;

    public Rank(String content) {
        if (!StringUtils.isEmpty(content)) {
            String[] item = content.split("\t");
            date = LocalDate.now();
            code = item[0];
            score = new BigDecimal(item[1]);
        }
    }
}

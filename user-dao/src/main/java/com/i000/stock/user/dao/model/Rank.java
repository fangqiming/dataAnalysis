package com.i000.stock.user.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
            System.out.println(content);
            String[] items = content.split(",");
            date = LocalDate.parse(items[0], DateTimeFormatter.ofPattern("yyyyMMdd"));
            code = items[1];
            score = new BigDecimal(items[9]);
        }
    }
}

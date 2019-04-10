package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class PpiVO {

    private List<String> legendTitle;

    private String title;

    private List<String> date;

    private List<BigDecimal> month;

    private List<BigDecimal> total;

    public PpiVO() {
        legendTitle = Arrays.asList("当月", "累计");
        date = new ArrayList<>();
        month = new ArrayList<>();
        total = new ArrayList<>();
    }
}

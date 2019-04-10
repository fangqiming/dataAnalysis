package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class NewStockAccountVO {

    private List<String> legendTitle;

    private String title;

    private List<String> date;

    private List<BigDecimal> amount;

    public NewStockAccountVO() {
        legendTitle = Arrays.asList("数量");
        date = new ArrayList<>();
        amount = new ArrayList<>();
    }
}

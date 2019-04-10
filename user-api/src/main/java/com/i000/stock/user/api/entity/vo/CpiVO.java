package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class CpiVO {

    private List<String> legendTitle;

    private String title;

    private List<String> date;

    private List<BigDecimal> total;

    private List<BigDecimal> city;

    private List<BigDecimal> countryside;

    public CpiVO() {
        legendTitle = Arrays.asList("全国", "城市", "农村");
        date = new ArrayList<>();
        total = new ArrayList<>();
        city = new ArrayList<>();
        countryside = new ArrayList<>();
    }
}

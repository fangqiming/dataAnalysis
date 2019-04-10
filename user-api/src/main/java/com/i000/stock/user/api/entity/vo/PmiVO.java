package com.i000.stock.user.api.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class PmiVO {
    private String title;

    private List<String> legendTitle;

    private List<String> date;

    private List<BigDecimal> industry;

    private List<BigDecimal> noIndustry;

    public PmiVO() {
        legendTitle = Arrays.asList("制造业", "非制造业");
        date = new ArrayList<>();
        industry = new ArrayList<>();
        noIndustry = new ArrayList<>();
    }
}

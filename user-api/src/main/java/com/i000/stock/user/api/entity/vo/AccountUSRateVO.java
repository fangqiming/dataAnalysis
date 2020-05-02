package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountUSRateVO {

    private List<Double> xyzq;

    private List<Double> jxzq;

    private List<Double> jxira;

    private List<Double> lhzq;

    private List<Double> gmg;

    private List<Double> sp;

    private List<String> date;

    public AccountUSRateVO() {
        xyzq = new ArrayList<>();
        jxzq = new ArrayList<>();
        jxira = new ArrayList<>();
        lhzq = new ArrayList<>();
        gmg = new ArrayList<>();
        sp = new ArrayList<>();
        date = new ArrayList<>();
    }
}

package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class AccountCNRateVO {

    private List<Double> lqjj;

    private List<Double> jhai;

    private List<Double> yczh;

    private List<Double> szzs;

    private List<Double> gag;

    private List<String> date;

    public AccountCNRateVO() {
        lqjj = new ArrayList<>();
        yczh = new ArrayList<>();
        jhai = new ArrayList<>();
        szzs = new ArrayList<>();
        gag = new ArrayList<>();
        date = new ArrayList<>();

    }
}

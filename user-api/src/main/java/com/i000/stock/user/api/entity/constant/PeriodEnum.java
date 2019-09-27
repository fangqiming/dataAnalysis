package com.i000.stock.user.api.entity.constant;

import lombok.Getter;

@Getter
public enum PeriodEnum {

    DAY_1("1d", "天"),
    WEEK_1("1w", "周");

    private String value;
    private String message;

    PeriodEnum(String value, String message) {
        this.message = message;
        this.value = value;
    }


}

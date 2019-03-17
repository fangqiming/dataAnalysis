package com.i000.stock.user.core.constant.enums;

import lombok.Getter;

@Getter
public enum TimeZoneEnum {

    NEW_YORK(1, -5, "纽约时间"),
    BEI_JING(2, 8, "北京时间"),;

    private Integer code;
    private Float value;
    private String message;

    TimeZoneEnum(Integer code, float value, String message) {
        this.code = code;
        this.value = value;
        this.message = message;
    }
}

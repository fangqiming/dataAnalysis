package com.i000.stock.user.api.entity.constant;

import lombok.Getter;

@Getter
public enum AuthEnum {

    A_STOCK(1, "AS", "A股访问权限"),
    US_STOCK(2, "US", "美股访问权限"),
    A_DIAGNOSIS(3, "AD", "A股诊股的权限"),
    A_RANK(3, "AR", "A股的排名权限"),;

    private Integer code;
    private String value;
    private String message;

    AuthEnum(Integer code, String value, String message) {
        this.code = code;
        this.value = value;
        this.message = message;
    }
}

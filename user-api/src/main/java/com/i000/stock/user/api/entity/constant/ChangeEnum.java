package com.i000.stock.user.api.entity.constant;

import lombok.Getter;

@Getter
public enum ChangeEnum {

    PLUS_MINUS(-1, "由正转负"),
    MINUS_PLUS(1, "由负转正"),
    PLUS_NO_CHANGE(2, "状态为正，未转变"),
    MINUS_NO_CHANGE(-2, "状态为负，未转变");

    private Integer code;
    private String message;

    ChangeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}

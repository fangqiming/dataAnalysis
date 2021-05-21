package com.i000.stock.user.api.entity.bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cache<T> {

    private T data;

    private LocalDateTime time;
}

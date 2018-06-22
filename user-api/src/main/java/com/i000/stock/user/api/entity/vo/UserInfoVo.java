package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateTimeSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:47 2018/5/8
 * @Modified By:
 */
@Data
public class UserInfoVo {

    private Long id;
    private String name;
    private String accountNo;
    private String password;
    private String mailNo;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedTime;
    private BigDecimal initAmount;
    private BigDecimal initNum;
}
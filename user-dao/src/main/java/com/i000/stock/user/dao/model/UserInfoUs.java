package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserInfoUs {

    @TableId
    private Long id;

    /**
     * 用户码
     */
    private String user;

    /**
     * 份数
     */
    private BigDecimal amount;

    /**
     * 账户开始日期
     */
    private LocalDate date;

    /**
     * 资金的份数
     */
    private Integer share;
}

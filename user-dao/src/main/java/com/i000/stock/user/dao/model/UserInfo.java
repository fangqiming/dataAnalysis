package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:13 2018/5/2
 * @Modified By:
 */
@Data
public class UserInfo {

    @TableId
    private Long id;
    private String name;
    private String accountNo;
    private String password;
    private String mailNo;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private BigDecimal initAmount;
    private BigDecimal initNum;
}

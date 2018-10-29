package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:10 2018/10/26
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReverseRepo {

    @TableId
    private Long id;

    private String code;

    private LocalDate date;

    private BigDecimal amount;

    private BigDecimal share;

    private BigDecimal gain;

    private BigDecimal profit;

    private String userCode;

}

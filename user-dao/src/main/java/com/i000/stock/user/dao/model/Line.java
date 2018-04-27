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
 * @Date:Created in 15:09 2018/4/26
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Line {

    @TableId
    private Long id;
    private LocalDate date;
    private BigDecimal baseMarket;
    private BigDecimal aiMarket;
    private String type;
    private BigDecimal gain;

}

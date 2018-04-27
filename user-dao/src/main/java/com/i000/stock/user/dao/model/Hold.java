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
 * @Date:Created in 15:10 2018/4/26
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hold {

    @TableId
    private Long id;
    private String name;
    private LocalDate oldDate;
    private BigDecimal oldPrice;
    private BigDecimal oldRank;
    private LocalDate newDate;
    private BigDecimal newPrice;
    private BigDecimal newRank;
    private Integer holdDay;
    private BigDecimal gain;
    private String type;

}

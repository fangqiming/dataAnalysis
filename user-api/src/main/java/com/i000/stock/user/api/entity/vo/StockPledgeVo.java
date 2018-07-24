package com.i000.stock.user.api.entity.vo;

import com.baomidou.mybatisplus.annotations.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.i000.stock.user.core.jackson.serialize.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:11 2018/7/18
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockPledgeVo {

    @TableId
    private Long id;

    /**
     * 公司名称
     */
    private String name;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 公布日期
     */
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    /**
     * 质押笔数
     */
    private Integer pledgeNumber;

    /**
     * 有限质押份数(万)
     */
    private BigDecimal limitedPledge;

    /**
     * 无限质押份数(万)
     */
    private BigDecimal unlimitedPledge;

    /**
     * 总股本（万）
     */
    private BigDecimal total;

    /**
     * 质押率
     */
    private BigDecimal rate;
}

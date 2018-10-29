package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
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
@AllArgsConstructor
@NoArgsConstructor
public class Asset {

    @TableId
    private Long id;

    private LocalDate date;

    /**
     * 股票余额
     */
    private BigDecimal stock;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 做空金额
     */
    private BigDecimal cover;


    /**
     * 相对上一天的收益率
     */
    private BigDecimal gain;

    /**
     * 用户码
     */
    private String userCode;

    /**
     * 总收益率 == 当逆回购完成后，总的收益率会变大 也就是在股票更新后，
     */
    private BigDecimal totalGain;

    private BigDecimal totalRepoProfit;
    private BigDecimal totalRepoAmount;
    private BigDecimal todayRepoProfit;
    private BigDecimal todayRepoAmount;

    /**
     * 是否已经逆回购的标记
     */
    private Byte isRepo;


}

package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.language.DoubleMetaphone;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountAsset {

    @TableId
    private Long id;

    /**
     * 账户名
     */
    private String accountName;

    /**
     * 净资产
     */
    private Double total;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 类型
     * 1.普通账户
     * 2.信用账户
     */
    private String type;

    /**
     * 份额
     */
    private Double share;

    /**
     * 账户的国籍 CN中国  US美国
     */
    private String country;

    /**
     * 仓位,注意此仓位仅仅用于数据补充时的过度作用
     */
    private Double position;
}

package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountMap {

    @TableId
    private Long id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 账户名
     */
    private String account;
}

package com.i000.stock.user.api.entity.vo;

import lombok.Data;

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
    private Integer canSee;
}

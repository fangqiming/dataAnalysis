package com.i000.stock.user.api.entity.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:50 2018/5/8
 * @Modified By:
 */
@Data
public class UserInfoLoginVo {

    @NotBlank(message = "用户名不能为空")
    private String name;

    @NotBlank(message = "密码不能为空")
    private String password;
}

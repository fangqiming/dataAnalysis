package com.i000.stock.user.api.entity.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:38 2018/5/8
 * @Modified By:
 */
@Data
public class UserInfoRegisterVo {

    @NotNull(message = "用户名不能为空")
    private String name;

    @NotNull(message = "手机号不能为空")
    @Pattern(regexp = "^1[0-9]{10}$", message = "手机号不符合规则")
    private String phone;

    @Length(min = 3, max = 20, message = "密码长度需要在3-20个字符之间")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Length(min = 1, max = 5, message = "性别长度错误")
    @NotBlank(message = "性别不能为空")
    private String gender;

    private String familyName;

}

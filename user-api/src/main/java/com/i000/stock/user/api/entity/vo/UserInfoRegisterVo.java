package com.i000.stock.user.api.entity.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:38 2018/5/8
 * @Modified By:
 */
@Data
public class UserInfoRegisterVo {

    @Length(min = 1, max = 20, message = "用户名长度应该1-20个字符之间")
    @NotBlank(message = "用户名不能为空")
    private String name;

    @Length(min = 3, max = 20, message = "密码长度需要在3-20个字符之间")
    @NotBlank(message = "密码不能为空")
    private String password;

    @NotNull(message = "初始金额不能为空")
    private BigDecimal initAmount;

    @NotNull(message = "初始份数不能为空")
    private BigDecimal initNum;

    @NotNull(message = "是否允许融资不能为空")
    private Byte isLeverage;
}

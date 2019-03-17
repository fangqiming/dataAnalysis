package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:41 2018/10/10
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLogin {

    @TableId
    private Long id;
    /**
     * 用户名
     */
    private String name;

    private String phone;

    /**
     * 密码
     */
    private String password;
    /**
     * 姓氏
     */
    private String familyName;
    /**
     * 性别
     */
    private String gender;

    private LocalDateTime createTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 随机码
     */
    private String accessCode;

    /**
     * 权限码 AS 表示A股  USS表示美股  AD 表示诊股权限
     */
    private String authority;
}

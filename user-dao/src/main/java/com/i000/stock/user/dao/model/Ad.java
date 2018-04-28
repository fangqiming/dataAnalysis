package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:13 2018/4/28
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ad {
    @TableId
    private Long id;
    @NotNull(message = "key不能为空")
    private String key;
    @NotNull(message = "value不能为空")
    private String value;
}

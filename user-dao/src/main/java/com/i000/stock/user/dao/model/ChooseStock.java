package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:53 2018/7/24
 * @Modified By:
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChooseStock {

    @TableId
    private Long id;
    private String code;
    private String name;
}

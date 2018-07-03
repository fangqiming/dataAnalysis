package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:18 2018/7/3
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperateSummary {

    @TableId
    private Integer id;
    private Integer sellNumber;
    private Integer buyNumber;
    private Integer profitNumber;
    private Integer lossNumber;
    private Integer holdNumber;
}

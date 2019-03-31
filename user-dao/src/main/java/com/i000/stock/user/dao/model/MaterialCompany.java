package com.i000.stock.user.dao.model;

import com.baomidou.mybatisplus.annotations.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCompany {
    @TableId
    private Long id;

    /**
     * 物料名称
     */
    private String name;

    /**
     * 公司名,多个公司以逗号分隔
     */
    private String companyName;

    /**
     * 公司代码,多个代码以逗号分隔
     */
    private String companyCode;

    /**
     * 下游公司名,多个公司以逗号分隔
     */
    private String downstreamName;

    /**
     * 下游公司代码,多个代码以逗号分隔
     */
    private String downstreamCode;
}

package com.i000.stock.user.dao.bo;

import com.i000.stock.user.core.constant.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:26 2018/1/20
 * @Modified By:
 */
@Getter
@AllArgsConstructor
public enum StepEnum implements BaseEnum {

    WEEK(0L, 7L, "", "周"),
    MONTH(1L, 30L, "", "月"),
    QUARTER(2L, 120L, "%Y_%u", "季"),
    YEAR(3L, 360L, "%Y%m", "年");

    private Long code;
    private Long step;
    private String groupCondition;
    private String msg;


}

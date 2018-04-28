package com.i000.stock.user.api.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:57 2018/1/20
 * @Modified By:
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseLineTrendVO {

    private List<BigDecimal> baseMarket;
    private List<BigDecimal> aiMarket;

    private List<String> time;

    public BaseLineTrendVO() {
        baseMarket = new ArrayList<>();
        aiMarket = new ArrayList<>();
        time = new ArrayList<>();
    }

}

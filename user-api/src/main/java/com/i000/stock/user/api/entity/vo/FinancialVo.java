package com.i000.stock.user.api.entity.vo;

import com.i000.stock.user.api.entity.bo.FinancialFiveBo;
import lombok.Data;

import java.util.List;

@Data
public class FinancialVo {

    private List<FinancialFiveBo> financial;

    private List<String> title;
}

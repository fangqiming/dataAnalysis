package com.i000.stock.user.api.entity.vo;

import com.i000.stock.user.api.entity.bo.AIIndexBo;
import com.i000.stock.user.api.entity.bo.FinancialRiskBo;
import com.i000.stock.user.api.entity.bo.OperateBo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisVo {

    private String code;

    private String name;

    private AIIndexBo aiIndexBo;

    private FinancialRiskBo financialRiskBo;

    private OperateBo operateBo;
}

package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JQKlineBo {

    private String date;
    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private Double volume;
    private Double money;
    private String code;
    private Double preClose;
}

package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockChangeDataBO {

    private String rq;
    private String bdr;
    private String bdsl;
    private String jcgp;
    private String jjjj;
    private String djgg;
    private String ygggx;
    private String gfbdtj;
}

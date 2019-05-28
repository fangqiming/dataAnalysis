package com.i000.stock.user.api.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockChangeDataBO {
    private String symbol;
    private String share_changer_name;
    private Long chg_date;
    private BigDecimal chg_shares_num;
    private BigDecimal trans_avg_price;
    private BigDecimal daily_shares_balance_otd;
    private String rr_of_chgr_and_manage;
    private String duty;
}

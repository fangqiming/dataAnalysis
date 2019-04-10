package com.i000.stock.user.api.entity.bo;

import lombok.Data;

import java.util.List;

@Data
public class StockChangeDataListBO {

    private List<StockChangeDataBO> rptShareHeldChangeList;
}

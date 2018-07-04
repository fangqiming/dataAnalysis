package com.i000.stock.user.api.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:24 2018/7/4
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageTradeRecordVo {
    List<TradeRecordVo> trade;
    AssetVo asset;
}

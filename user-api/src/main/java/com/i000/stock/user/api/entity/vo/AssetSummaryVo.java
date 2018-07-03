package com.i000.stock.user.api.entity.vo;

import com.i000.stock.user.api.entity.bo.EndAssetBo;
import com.i000.stock.user.api.entity.bo.StartAssetBo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:52 2018/7/3
 * @Modified By:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetSummaryVo {
    private StartAssetBo start;
    private EndAssetBo end;
}

package com.i000.stock.user.api.entity.bo;

import com.i000.stock.user.dao.model.Hold;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:52 2018/5/2
 * @Modified By:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeDetailBo {

    private List<Hold> buy = new ArrayList<>();

    private List<Hold> sell = new ArrayList<>();

    private List<Hold> shorts = new ArrayList<>();

    private List<Hold> cover = new ArrayList<>();
}

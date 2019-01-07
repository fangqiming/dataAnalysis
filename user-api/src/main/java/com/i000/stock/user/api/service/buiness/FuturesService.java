package com.i000.stock.user.api.service.buiness;

import java.math.BigDecimal;

public interface FuturesService {

    /**
     * 获取当前的仓位
     *
     * @param isContainRecommend 是否包含推荐信息的
     * @return
     */
    BigDecimal getPosition(boolean isContainRecommend);

    /**
     * 做空沪深
     * 需要根据仓位的变动，来动态的查询做空的份数
     */
    void shortIf();

    /**
     * 对沪深300进行平仓
     */
    void coverIf();

    /**
     * 对沪深300进行强制平仓
     */
    void forceCoverIf();
}

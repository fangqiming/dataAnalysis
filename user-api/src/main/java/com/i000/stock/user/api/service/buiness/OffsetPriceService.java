package com.i000.stock.user.api.service.buiness;


/**
 * @Author:qmfang
 * @Description: 用来计算股价偏移的服务 也就是处理拆股的问题
 * @Date:Created in 13:51 2018/6/21
 * @Modified By:
 */
public interface OffsetPriceService {

    void updateAmount(StringBuffer stringBuffer);
}

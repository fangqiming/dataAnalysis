package com.i000.stock.user.api.service.external;

import com.i000.stock.user.api.entity.vo.StockPledgeVo;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.PageResult;
import com.i000.stock.user.dao.model.StockPledge;

import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:38 2018/7/17
 * @Modified By:
 */
public interface StockPledgeService {

    /**
     * 保存上证的股票质押信息
     */
    List<StockPledge> save(String... date) throws Exception;

    /**
     * 分页查询股权质押接口
     *
     * @param baseSearchVo
     * @return
     */
    PageResult<StockPledgeVo> search(BaseSearchVo baseSearchVo, String code, String name);

    /**
     * 根据股票代码获取股票质押率
     *
     * @param code
     * @return
     */
    StockPledge getByCode(String code);

}

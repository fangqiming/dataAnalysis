package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.api.entity.bo.IndexValueBo;
import com.i000.stock.user.api.entity.bo.PageIndexValueBo;
import com.i000.stock.user.api.entity.vo.GainVo;
import com.i000.stock.user.dao.model.IndexGain;

import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description: 指数收益服务接口
 * @Date:Created in 14:23 2018/7/3
 * @Modified By:
 */
public interface IndexGainService {

    /**
     * 保存指数信息
     *
     * @param indexGain
     */
    void save(IndexGain indexGain);

    /**
     * 根据指数信息计算出 IndexGain对象
     *
     * @param indexValueBo
     * @return
     */
    IndexGain calculateIndexInfo(IndexValueBo indexValueBo);

    /**
     * 查询出全部的指数价格信息
     *
     * @return
     */
    List<IndexGain> find();

    /**
     * 查询距离当前日期前多少天的记录
     *
     * @param date
     * @param diff
     * @return
     */
    IndexGain getDiff(LocalDate date, Integer diff);

    /**
     * 获取距离date前多少天的收益率
     *
     * @param date
     * @param diff
     * @return
     */
    PageIndexValueBo getDiffGain(LocalDate date, Integer diff);
}

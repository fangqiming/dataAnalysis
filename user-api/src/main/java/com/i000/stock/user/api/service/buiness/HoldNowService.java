package com.i000.stock.user.api.service.buiness;

import com.i000.stock.user.dao.model.HoldNow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 11:50 2018/5/2
 * @Modified By:
 */
public interface HoldNowService {

    /**
     * 保存买入/做空的股票
     *
     * @param holdNow
     */
    void save(HoldNow holdNow);


    /**
     * 获取当前的持股
     *
     * @param userCode
     * @param name
     * @param date
     * @param type
     * @return
     */
    HoldNow getByNameDateType(String userCode, String name, LocalDate date, String type);

    /**
     * 通过id删除对象
     *
     * @param id
     * @return
     */
    Integer deleteById(Long id);

    /**
     * 获取指定用户的当前持股情况
     *
     * @param userCode
     * @return
     */
    List<HoldNow> find(String userCode);

    /**
     * 通过股票名称更新股票价格
     *
     * @return
     */
    Integer updatePrice(LocalDate date);


    /**
     * 更新股票的份数
     */

    Integer updateAmount(BigDecimal rate, String code, LocalDate date);

    void updateAmountPriceByName(BigDecimal price, BigDecimal amount, String name,String userCode);


}

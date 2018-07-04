package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.OperateSummary;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 18:21 2018/7/3
 * @Modified By:
 */
public interface OperateSummaryMapper extends BaseMapper<OperateSummary> {

    /**
     * 卖
     *
     * @param holdDay
     * @param profit
     * @param loss
     */
    @Update("update operate_summary set sell_number=sell_number+1 , hold_number=hold_number+${holdDay},profit_number=profit_number+${profit},loss_number=loss_number+${loss}  where user_code=#{userCode}")
    void updateSell(@Param("holdDay") Integer holdDay, @Param("profit") Integer profit, @Param("loss") Integer loss, @Param("userCode") String userCode);

    /**
     * 买
     */
    @Update("update operate_summary set buy_number=buy_number+1 where user_code=#{userCode}")
    void updateBuy(@Param("userCode") String userCode);

    @Select("select count(*) from operate_summary where user_code=#{userCode}")
    Integer getUserCodeNumber(@Param("userCode") String userCode);

    OperateSummary getByUserCode(@Param("userCode") String userCode);
}

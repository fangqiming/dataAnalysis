package com.i000.stock.user.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.i000.stock.user.dao.model.HoldNow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:49 2018/5/2
 * @Modified By:
 */
public interface HoldNowMapper extends BaseMapper<HoldNow> {


    /**
     * 根据信息获取指定的当前持有的股票
     *
     * @param userCode
     * @param name
     * @param date
     * @param type
     * @return
     */
    HoldNow getByNameDateType(@Param("userCode") String userCode, @Param("name") String name,
                              @Param("date") LocalDate date, @Param("type") String type);

    /**
     * 获取指定用户的持股信息
     *
     * @param userCode
     * @return
     */
    List<HoldNow> find(@Param("userCode") String userCode);

    /**
     * 根据股票名称更新股票的价格
     *
     * @param name
     * @param price
     * @return
     */
    void updatePrice(@Param("price") BigDecimal price, @Param("name") String name, @Param("date") LocalDate date);

    /**
     * 更新股票的数量==>应对拆股与并股
     *
     * @param rate
     * @param code
     * @param date
     * @return
     */
    @Update("update hold_now set amount=amount*#{rate} where `name`=#{code} and new_date=#{date}")
    Integer updateAmount(@Param("rate") BigDecimal rate, @Param("code") String code, @Param("date") LocalDate date);

    @Select("select count(*) from hold_now where user_code = #{name};")
    Integer getCount(String name);
}

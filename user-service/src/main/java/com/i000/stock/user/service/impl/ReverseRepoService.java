package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.RepoProfitBO;
import com.i000.stock.user.api.service.util.EmailService;
import com.i000.stock.user.api.service.util.IndexPriceCacheService;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.mapper.HolidayMapper;
import com.i000.stock.user.dao.mapper.ReverseRepoMapper;
import com.i000.stock.user.dao.model.Holiday;
import com.i000.stock.user.dao.model.ReverseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:23 2018/10/26
 * @Modified By:
 */
@Service
public class ReverseRepoService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReverseRepoMapper reverseRepoMapper;

    @Autowired
    private HolidayMapper holidayMapper;

    @Autowired
    private IndexPriceCacheService indexPriceCacheService;

    private final String GC001 = "sh204001";

    private final BigDecimal MIN_AMOUNT = new BigDecimal("100000");

    private static Map<LocalDate, BigDecimal> cache = new HashMap<>(1);


    /**
     * 根据金额和日期获取逆回购的收益值
     *
     * @param date
     * @param amount
     * @return
     */
    public RepoProfitBO getProfitDaysByDate(LocalDate date, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return RepoProfitBO.builder().amount(BigDecimal.ZERO).profit(BigDecimal.ZERO).gian(BigDecimal.ZERO).code("GC001").build();
        }
        amount = getAmount(amount);
        BigDecimal gain = getRepoGain(date);
        Integer days = getDaysByDate(date);
        BigDecimal profit = amount.multiply(gain).multiply(new BigDecimal(days))
                .divide(new BigDecimal(36500), 0, BigDecimal.ROUND_UP);
        return RepoProfitBO.builder().amount(amount).profit(profit).gian(gain).code("GC001").build();
    }

    /**
     * 缓存GC001的收益率
     *
     * @param date
     * @return
     */
    private BigDecimal getRepoGain(LocalDate date) {
        if (cache.containsKey(date)) {
            return cache.get(date);
        }
        BigDecimal gain = indexPriceCacheService.getOnePrice(GC001, 10);
        cache.put(date, gain);
        return gain;
    }

    public BigDecimal getAmount(BigDecimal amount) {
        return amount.divide(MIN_AMOUNT, 0, BigDecimal.ROUND_DOWN).multiply(MIN_AMOUNT);
    }

    public Page<ReverseRepo> search(String userCode, BaseSearchVo baseSearchVo) {
        baseSearchVo.setStart();
        List<ReverseRepo> data = reverseRepoMapper.search(userCode, baseSearchVo);
        Long total = reverseRepoMapper.pageTotal();
        Page<ReverseRepo> result = new Page<>();
        result.setTotal(total);
        result.setList(data);
        return result;
    }


    public void save(ReverseRepo reverseRepo) {
        //保存逆回购的记录
        reverseRepoMapper.insert(reverseRepo);
    }


    /**
     * 根据传入的日期，获取该日期对应的计息天数
     *
     * @param date
     * @return
     */
    private Integer getDaysByDate(LocalDate date) {
        List<Holiday> days = holidayMapper.getTwentyDaysByDate(date);
        if (Objects.isNull(days) || days.size() <= 15) {
            emailService.sendMail("需要更新节假日记录", "5天后家假日数据将缺失，请及时按照国家公布的家假日信息更新此表", true);
        }
        int result = 1;
        Holiday now = days.get(0);
        if (!isWork(now)) {
            return 0;
        }
        Holiday tomorrow = days.get(1);
        Holiday afterTomorrow = days.get(2);
        if (isWork(tomorrow) && !isWork(afterTomorrow)) {
            for (int i = 2; i < days.size(); i++) {
                Holiday temp = days.get(i);
                if (isWork(temp)) {
                    break;
                }
                result++;
            }
        }
        return result;
    }

    private boolean isWork(Holiday tomorrow) {
        return tomorrow.getIsWorking() == 1;
    }
}

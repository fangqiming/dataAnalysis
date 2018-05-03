package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.dao.mapper.HoldMapper;
import com.i000.stock.user.dao.model.Hold;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 9:54 2018/4/28
 * @Modified By:
 */
@Component
@Transactional
public class HoldServiceImpl implements HoldService {

    @Resource
    private HoldMapper holdMapper;

    @Override
    public List<Hold> findHold() {
        LocalDate maxDate = holdMapper.getMaxDate();
        return Objects.isNull(maxDate) ? new ArrayList<>(0) :
                holdMapper.findByDate(maxDate);
    }

    @Override
    public List<Hold> getTrade() {
        List<Hold> result = new ArrayList<>();
        List<LocalDate> twoDay = holdMapper.findTwoDay();
        if (!CollectionUtils.isEmpty(twoDay)) {
            if (twoDay.size() == 1) {
                //初始情况
                List<Hold> today = holdMapper.findByDate(twoDay.get(0));
                List<Hold> aLong = today.stream().filter(a -> a.getType().contains("LONG")).collect(toList());
                aLong.forEach(a -> a.setAction("BUY"));
                result.addAll(aLong);

                List<Hold> aShort = today.stream().filter(a -> a.getType().equals("SHORT")).collect(toList());
                aShort.forEach(a -> a.setAction("SHORT"));
                result.addAll(aShort);
            } else {
                //当天持股
                Optional<LocalDate> max = twoDay.stream().max(LocalDate::compareTo);
                List<Hold> today = holdMapper.findByDate(max.get());
                //上次持股
                Optional<LocalDate> min = twoDay.stream().min(LocalDate::compareTo);
                List<Hold> yesterday = holdMapper.findByDate(min.get());
                for (Hold hold : today) {
                    if (!yesterday.contains(hold)) {
                        //今天相对昨天多了一个股票==>推出是今天买入的
                        if (hold.getType().contains("LONG")) {
                            hold.setAction("BUY");
                            result.add(hold);
                        }
                        //今天相对昨天多了一个做空==>推出今天的是做空得到的
                        if (hold.getType().equals("SHORT")) {
                            hold.setAction("SHORT");
                            result.add(hold);
                        }
                    }
                }
                for (Hold hold : yesterday) {
                    if (!today.contains(hold)) {
                        if (hold.getType().contains("LONG")) {
                            hold.setAction("SELL");
                            result.add(hold);
                        }
                        if (hold.getType().equals("SHORT")) {
                            hold.setAction("COVER");
                            result.add(hold);
                        }
                    }
                }
            }
        }
        return result;
    }
}

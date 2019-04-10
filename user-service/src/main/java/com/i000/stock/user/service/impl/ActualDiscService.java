package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.i000.stock.user.api.entity.vo.*;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.mapper.ActualDiscMapper;
import com.i000.stock.user.dao.model.ActualDisc;
import com.i000.stock.user.dao.model.IndexUs;
import com.i000.stock.user.dao.model.IndexValue;
import com.i000.stock.user.service.impl.us.service.IndexUSService;
import com.i000.stock.user.service.impl.us.service.UsGainRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActualDiscService {

    @Autowired
    private ActualDiscMapper actualDiscMapper;

    @Autowired
    private UsGainRateService usGainRateService;

    @Autowired
    private IndexValueService indexValueService;

    @Autowired
    private IndexUSService indexUSService;

    public void save(ActualDisc actualDisc) {
        actualDiscMapper.insert(actualDisc);
    }

    public List<ActualDisc> findByType(String type) {
        EntityWrapper<ActualDisc> ew = new EntityWrapper<>();
        ew.where("type = {0}", type);
        return actualDiscMapper.selectList(ew);
    }

    public ActualDiscVO findActual(String type) {
        List<ActualDisc> ac = findByType(type);
        List<ActualDiscBO> list = new ArrayList<>();
        ActualDiscVO result = new ActualDiscVO();
        Map<String, List<ActualDisc>> group = ac.stream().collect(Collectors.groupingBy(ActualDisc::getName));
        for (Map.Entry<String, List<ActualDisc>> entry : group.entrySet()) {
            List<ActualDisc> discs = entry.getValue();
            discs.sort(Comparator.comparing(ActualDisc::getDate));
            result.setDate(discs.get(discs.size() - 1).getDate());
            ActualDisc last = discs.get(discs.size() - 1);
            ActualDisc init = discs.get(0);
            List<BigDecimal> netWorths = discs.stream().map(a -> a.getNetWorth()).collect(Collectors.toList());
            BigDecimal withdrawal = usGainRateService.calcMaxDd(netWorths);
            ActualDiscBO actualDiscBO = ActualDiscBO.builder().name(entry.getKey())
                    .marketCap(last.getMarketCap().setScale(0, BigDecimal.ROUND_UP))
                    .netWorth(last.getNetWorth()).startDate(init.getDate())
                    .initNetWorth(new BigDecimal(1)).withdrawal(withdrawal).build();
            list.add(actualDiscBO);
        }
        result.setList(list);
        return result;
    }

    /**
     * 问题，1.需要倒序显示
     * 2.周收益的计算错误
     *
     * @param name
     * @param baseSearchVo
     * @return
     */
    public ActualDiscDetailVO getDetailByName(String name, BaseSearchVo baseSearchVo) {
        List<ActualDiscDetailBO> list = new ArrayList<>();
        EntityWrapper<ActualDisc> ew = new EntityWrapper<>();
        ew.where("name = {0}", name);
        ew.orderBy("date",false);

        Page page = new Page(baseSearchVo.getPageNo(), baseSearchVo.getPageSize());
        //获取了实盘数据，采用的倒序
        List<ActualDisc> discs = actualDiscMapper.selectPage(page, ew);

        if (!CollectionUtils.isEmpty(discs)) {
            Collections.reverse(discs);
            ActualDisc init = getBeforeDate(discs.get(0).getName(), discs.get(0).getDate());
            for (int i = 0; i < discs.size(); i++) {
                ActualDisc actual = discs.get(i);
                BigDecimal before = i - 1 < 0 ? init.getNetWorth() : discs.get(i - 1).getNetWorth();
                BigDecimal weekRate = (actual.getNetWorth().subtract(before)).divide(before, 4, BigDecimal.ROUND_UP);
                ActualDiscDetailBO detail = ActualDiscDetailBO.builder().date(actual.getDate())
                        .marketCap(actual.getMarketCap()).netWorth(actual.getNetWorth()).weekRate(weekRate).build();
                list.add(detail);
            }
            Collections.reverse(list);
        }
        Long total = actualDiscMapper.getCountByName(name);
        return ActualDiscDetailVO.builder().name(name).list(list).total(total).build();
    }

    public BaseLineTrendVO getTrendByName(String name) {
        EntityWrapper<ActualDisc> ew = new EntityWrapper<>();
        ew.where("name = {0}", name);
        List<ActualDisc> discs = actualDiscMapper.selectList(ew);
        List<BigDecimal> ai = new ArrayList<>();
        List<BigDecimal> sz = new ArrayList<>();
        List<String> time = new ArrayList<>();
        if (!CollectionUtils.isEmpty(discs)) {
            discs.sort(Comparator.comparing(ActualDisc::getDate));
            String type = discs.get(0).getType();
            Map<LocalDate, BigDecimal> indexValue = getIndexByValue(type, discs.stream().map(a -> a.getDate())
                    .collect(Collectors.toList()));
            ActualDisc base = discs.get(0);
            sz.add(BigDecimal.ZERO);
            ai.add(BigDecimal.ZERO);
            time.add(base.getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
            BigDecimal baseSz = getInitSZ(type, base.getDate());
            for (int i = 1; i < discs.size(); i++) {

                ai.add((discs.get(i).getNetWorth().subtract(base.getNetWorth()))
                        .divide(base.getNetWorth(), 4, BigDecimal.ROUND_UP)
                        .multiply(new BigDecimal(100)));
                System.out.println(discs.get(i).getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
                System.out.println(indexValue.get(discs.get(i).getDate()));
                sz.add((indexValue.get(discs.get(i).getDate()).subtract(baseSz))
                        .divide(baseSz, 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100)));
                time.add(discs.get(i).getDate().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
            }
        }
        return BaseLineTrendVO.builder().aiMarket(ai).baseMarket(sz).time(time).build();
    }

    public Map<LocalDate, BigDecimal> getIndexByValue(String type, List<LocalDate> dates) {
        Map<LocalDate, BigDecimal> result = new HashMap<>();
        if ("CN".equals(type)) {
            List<IndexValue> between = indexValueService.findByDates(dates);
            if (!CollectionUtils.isEmpty(between)) {
                for (IndexValue indexValue : between) {
                    result.put(indexValue.getDate(), indexValue.getSh());
                }

            }
        } else if ("US".equals(type)) {
            List<IndexUs> between = indexUSService.findByDate(dates);
            if (!CollectionUtils.isEmpty(between)) {
                for (IndexUs indexValue : between) {
                    result.put(indexValue.getDate(), indexValue.getSp500());
                }
            }
        }
        return result;
    }

    public BigDecimal getInitSZ(String type, LocalDate start) {
        if ("CN".equals(type)) {
            IndexValue before = indexValueService.getRecentlyByLt(start);
            return before.getSh();
        } else if ("US".equals(type)) {
            IndexUs ltDateOne = indexUSService.getLtDateOne(start);
            return ltDateOne.getSp500();
        }
        throw new ServiceException(ApplicationErrorMessage.ACTUAL_DISC_DATA_ERROR);
    }

    public ActualDisc getBeforeDate(String name, LocalDate date) {
        ActualDisc actualDisc = getL(name, date);
        if (Objects.isNull(actualDisc)) {
            actualDisc = getInit(name);
        }
        return actualDisc;
    }

    public ActualDisc getL(String name, LocalDate date) {
        EntityWrapper<ActualDisc> ew = new EntityWrapper<>();
        ew.where("name={0}", name)
                .and("date<{0}", date)
                .orderBy("date", false).last("limit 1");
        return getOne(ew);
    }

    private ActualDisc getInit(String name) {
        EntityWrapper<ActualDisc> ew = new EntityWrapper<>();
        ew.where("name={0}", name).orderBy("date").last("limit 1");
        return getOne(ew);
    }

    private ActualDisc getOne(EntityWrapper<ActualDisc> ew) {
        List<ActualDisc> discs = actualDiscMapper.selectList(ew);
        if (CollectionUtils.isEmpty(discs)) {
            return null;
        }
        return discs.get(0);
    }
}

package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.entity.vo.AccountCNRateVO;
import com.i000.stock.user.api.entity.vo.AccountDiffVO;
import com.i000.stock.user.api.entity.vo.AccountUSRateVO;
import com.i000.stock.user.api.entity.vo.AccountVO;
import com.i000.stock.user.api.service.buiness.AssetService;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.dao.bo.AccountAssetBO;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.AccountAssetService;
import com.i000.stock.user.service.impl.AccountService;
import com.i000.stock.user.service.impl.us.AccountHoldService;
import com.i000.stock.user.service.impl.us.service.AssetUsService;
import com.i000.stock.user.service.impl.us.service.IndexUSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    @Resource
    private AccountAssetService accountAssetService;

    @Resource
    private AccountService accountService;

    @Resource
    private AccountHoldService accountHoldService;

    @Resource
    private IndexValueService indexValueService;

    @Resource
    private IndexUSService indexUSService;

    @Resource
    private AssetService assetService;

    @Resource
    private AssetUsService assetUsService;

    private DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 获取A股当天的收益报告
     *
     * @return
     */
    @GetMapping(path = "/get_cn")
    public ResultEntity findCN() {
        List<AccountVO> cn = findCurrent("CN");
        if (!CollectionUtils.isEmpty(cn)) {
            String date = cn.get(0).getDate();
            rename(cn);
            return Results.newTimeResultEntity(date, cn);
        }
        return Results.newTimeResultEntity("yyyy-MM-dd", new ArrayList<>());
    }

    /**
     * 获取美股当天的收益报告
     *
     * @return
     */
    @GetMapping(path = "/get_us")
    public ResultEntity findUS() {
        List<AccountVO> us = findCurrent("US");
        if (!CollectionUtils.isEmpty(us)) {
            String date = us.get(0).getDate();
            rename(us);
            return Results.newTimeResultEntity(date, us);
        }
        return Results.newTimeResultEntity("yyyy-MM-dd", new ArrayList<>());
    }

    @GetMapping(path = "/get_diff_by_date")
    public ResultEntity findError(@RequestParam("start") String start, @RequestParam("end") String end) {
        LocalDate startDate = LocalDate.parse(start, DF);
        LocalDate endDate = LocalDate.parse(end, DF);
        List<AccountDiffVO> cn = find(startDate, endDate, "CN");
        List<AccountDiffVO> us = find(startDate, endDate, "US");
        cn.addAll(us);
        rename2(cn);
        if (!CollectionUtils.isEmpty(cn)) {
            String date = cn.get(0).getDate();
            return Results.newTimeResultEntity(date, cn);
        }
        return Results.newTimeResultEntity("[ERROR]时间范围过小", cn);

    }

    @GetMapping(path = "/get_diff_by_range")
    public ResultEntity findError(@RequestParam String diff) {
        //首先需要获取账户最新的日期.全部以A股为准
        LocalDate end = LocalDate.now();
        LocalDate start = null;
        if ("w".equalsIgnoreCase(diff)) {
            start = end.minusWeeks(1L);
        } else if ("m".equalsIgnoreCase(diff)) {
            start = end.minusMonths(1L);
        } else if ("q".equalsIgnoreCase(diff)) {
            start = end.minusMonths(3L);
        } else if ("y".equalsIgnoreCase(diff)) {
            start = end.minusYears(1L);
        }
        List<AccountDiffVO> cn = find(start, end, "CN");
        List<AccountDiffVO> us = find(start, end, "US");
        String date = cn.get(0).getDate();
        cn.addAll(us);
        rename2(cn);
        return Results.newTimeResultEntity(date, cn);
    }

    @GetMapping(path = "/get_cn_pictrue")
    public ResultEntity findCNPicture() {
        LocalDate end = accountAssetService.getCurrent("CN");
        LocalDate start = LocalDate.parse("2020-03-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        AccountCNRateVO result = accountService.getCn(start, end);
        return Results.newSingleResultEntity(result);
    }

    @GetMapping(path = "/get_us_pictrue")
    public ResultEntity findUSPicture() {
        LocalDate end = accountAssetService.getCurrent("US");
        LocalDate start = LocalDate.parse("2020-03-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        AccountUSRateVO result = accountService.getUS(start, end);
        return Results.newSingleResultEntity(result);
    }

    public void rename2(List<AccountDiffVO> account) {
        if (!CollectionUtils.isEmpty(account)) {
            for (AccountDiffVO accountVO : account) {
                String name = accountVO.getAccount();
                String accountName = accountService.getAccountName(name);
                accountVO.setAccount(accountName);
            }
        }
    }

    public void rename(List<AccountVO> account) {
        if (!CollectionUtils.isEmpty(account)) {
            for (AccountVO accountVO : account) {
                String name = accountVO.getName();
                String accountName = accountService.getAccountName(name);
                accountVO.setName(accountName);
            }
        }
    }


    public List<AccountDiffVO> find(LocalDate start, LocalDate end, String country) {
        List<AccountDiffVO> result = new ArrayList<>();
        //取值为null影响了后面的
        LocalDate beforeDate = accountAssetService.getBeforeDate(country, start);
        if (Objects.nonNull(beforeDate)) {
            List<AccountAssetBO> account = accountAssetService.findBetween(beforeDate, end, country);
            //注意此处有一堆账户,可能是A股或者美股
            for (AccountAssetBO a : account) {
                //追加仓位
                if (Objects.isNull(a.getPosition())) {
                    List<AccountHold> hold = accountHoldService.findHold(a.getAccountName(), a.getDate());
                    if (!CollectionUtils.isEmpty(hold)) {
                        double securityValue = hold.stream()
                                .filter(h -> !h.getCode().contains("标准券"))
                                .mapToDouble(h -> Math.abs(h.getQuantity()) * h.getPrice()).sum();
                        a.setPosition(securityValue / a.getTotal());
                    }
                }
            }
            Map<String, List<AccountAssetBO>> accountGroup = account.stream()
                    .collect(Collectors.groupingBy(AccountAssetBO::getAccountName));
            for (Map.Entry<String, List<AccountAssetBO>> entry : accountGroup.entrySet()) {
                AccountDiffVO tmp = new AccountDiffVO();
                List<AccountAssetBO> value = entry.getValue();
                value.sort(Comparator.comparing(AccountAssetBO::getDate));
                tmp.setAccount(value.get(0).getAccountName());
                tmp.setDate(value.get(value.size() - 1).getDate().format(DF));
                if (value.size() < 2) {
                    return new ArrayList<>();
                }
                double current = (value.get(value.size() - 1).getTotal()) / (value.get(value.size() - 1).getShare());
                double before = value.get(0).getTotal() / value.get(0).getShare();
                double gain = (current - before) / before;
                value.remove(0);
                List<BigDecimal> assets = value.stream().map(a -> BigDecimal.valueOf(a.getTotal())).collect(Collectors.toList());
                double maxDown = calcMaxDd(assets).doubleValue();
                tmp.setDrawdown(maxDown);
                tmp.setGain(gain);
                tmp.setPosition(value.stream().mapToDouble(a -> a.getPosition()).average().getAsDouble());
                tmp.setTotal(current);
                result.add(tmp);
            }
            AccountDiffVO base = getBase(beforeDate, end, country);
            AccountDiffVO index = getIndex(beforeDate, end, country);
            result.sort((a, b) -> b.getTotal().compareTo(a.getTotal()));
            result.add(0, base);
            result.add(1, index);
            for (AccountDiffVO vo : result) {
                double gainDiff = vo.getGain() - base.getGain();
                double maxdownDiff = vo.getDrawdown() - base.getDrawdown();
                vo.setDrawdownDiff(maxdownDiff);
                vo.setGainDiff(gainDiff);
                double acc = vo.getGain() / vo.getPosition();
                double ba = base.getGain() / base.getPosition();
                vo.setBeta((ba - acc) / (1 + ba));
            }
            return result;

        }
        return new ArrayList<>();
    }

    private AccountDiffVO getBase(LocalDate start, LocalDate end, String country) {
        AccountDiffVO result = new AccountDiffVO();
        if ("CN".equals(country)) {
            List<Asset> assetBetween = assetService.findAssetBetween("10000000", start, end);
            assetBetween.sort(Comparator.comparing(Asset::getDate));
            result.setAccount("勾A股");
            result.setDate(assetBetween.get(assetBetween.size() - 1).getDate().format(DF));
            Asset before = assetBetween.get(0);
            Asset current = assetBetween.get(assetBetween.size() - 1);
            double initValue = before.getStock().add(before.getBalance()).doubleValue();
            double currentValue = current.getStock().add(current.getBalance()).doubleValue();
            double gain = (currentValue - initValue) / initValue;
            result.setGain(gain);
            assetBetween.remove(0);
            double position = assetBetween.stream()
                    .mapToDouble(a -> a.getStock().divide(a.getBalance().add(a.getStock()), 4, BigDecimal.ROUND_UP).doubleValue())
                    .average().getAsDouble();
            result.setPosition(position);
            result.setTotal(currentValue);
            List<BigDecimal> assets = assetBetween.stream().map(a -> a.getBalance().add(a.getStock())).collect(Collectors.toList());
            BigDecimal maxDd = calcMaxDd(assets);
            result.setDrawdown(maxDd.doubleValue());
        } else {
            List<AssetUs> assetBetween = assetUsService.findBetweenDateByUser(start, end, "10000000");
            assetBetween.sort(Comparator.comparing(AssetUs::getDate));
            result.setAccount("勾美股");
            result.setDate(assetBetween.get(assetBetween.size() - 1).getDate().format(DF));
            AssetUs before = assetBetween.get(0);
            AssetUs current = assetBetween.get(assetBetween.size() - 1);
            double initValue = before.getStock().add(before.getBalance()).add(before.getCover()).doubleValue();
            double currentValue = current.getStock().add(current.getBalance()).add(current.getCover()).doubleValue();
            double gain = (currentValue - initValue) / initValue;
            result.setGain(gain);
            assetBetween.remove(0);
            double position = assetBetween.stream()
                    .mapToDouble(a -> (a.getStock().add(a.getCover().abs())).divide(a.getBalance().add(a.getStock()).add(a.getCover()), 4, BigDecimal.ROUND_UP).doubleValue())
                    .average().getAsDouble();
            result.setPosition(position);
            result.setTotal(currentValue);
            List<BigDecimal> assets = assetBetween.stream().map(a -> a.getBalance().add(a.getCover()).add(a.getStock())).collect(Collectors.toList());
            BigDecimal maxDd = calcMaxDd(assets);
            result.setDrawdown(maxDd.doubleValue());
        }
        return result;
    }

    private AccountDiffVO getIndex(LocalDate start, LocalDate end, String country) {
        AccountDiffVO result = new AccountDiffVO();
        if ("CN".equals(country)) {
            List<IndexValue> between = indexValueService.findBetween(start, end);
            between.sort(Comparator.comparing(IndexValue::getDate));
            double init = between.get(0).getSh().doubleValue();
            double current = between.get(between.size() - 1).getSh().doubleValue();
            result.setDate(between.get(between.size() - 1).getDate().format(DF));
            result.setAccount("上证指数");
            double gain = (current - init) / init;
            result.setGain(gain);
            result.setTotal(current);
            between.remove(0);
            List<BigDecimal> price = between.stream().map(a -> a.getSh()).collect(Collectors.toList());
            BigDecimal maxDd = calcMaxDd(price);
            result.setDrawdown(maxDd.doubleValue());
            result.setPosition(1.0);
        } else {
            List<IndexUs> between = indexUSService.findBetweenDate(start, end);
            between.sort(Comparator.comparing(IndexUs::getDate));
            double init = between.get(0).getSp500().doubleValue();
            double current = between.get(between.size() - 1).getSp500().doubleValue();
            result.setDate(between.get(between.size() - 1).getDate().format(DF));
            result.setAccount("标普500");
            double gain = (current - init) / init;
            result.setGain(gain);
            between.remove(0);
            List<BigDecimal> price = between.stream().map(a -> a.getSp500()).collect(Collectors.toList());
            BigDecimal maxDd = calcMaxDd(price);
            result.setTotal(current);
            result.setDrawdown(maxDd.doubleValue());
            result.setPosition(1.0);
        }
        return result;
    }

    private List<AccountVO> findCurrent(String country) {
        //获取全部的账户,并且获取到账户的仓位
        List<AccountVO> accountVOS = new ArrayList<>();
        List<AccountAssetBO> accounts = accountAssetService.findCurrentAndBefore(country);
        LocalDate date = accounts.stream().map(a -> a.getDate()).max(LocalDate::compareTo).get();
        String dateStr = date.format(DF);
        for (AccountAssetBO a : accounts) {
            //遍历账户
            if (Objects.isNull(a.getPosition())) {
                List<AccountHold> hold = accountHoldService.findHold(a.getAccountName(), a.getDate());
                if (!CollectionUtils.isEmpty(hold)) {
                    double securityValue = hold.stream()
                            .filter(h -> !h.getCode().contains("标准券"))
                            .mapToDouble(h -> Math.abs(h.getQuantity()) * h.getPrice()).sum();
                    a.setPosition(securityValue / a.getTotal());
                }
            }
        }
        //对账户进行分组
        Map<String, List<AccountAssetBO>> assetGroup = accounts.stream()
                .collect(Collectors.groupingBy(AccountAssetBO::getAccountName));
        //对账户进行遍历,再此之前需要获取,对比的账户(勾A股/美股)
        for (Map.Entry<String, List<AccountAssetBO>> entry : assetGroup.entrySet()) {
            //需要对账户的收益率进行计算
            List<AccountAssetBO> value = entry.getValue();
            if (value.size() == 2) {
                value.sort(Comparator.comparing(AccountAssetBO::getDate));  //
                AccountAssetBO before = value.get(0);
                AccountAssetBO current = value.get(1);
                double gain = (current.getTotal() / current.getShare() - before.getTotal() / before.getShare())
                        / (before.getTotal() / before.getShare());
                accountVOS.add(AccountVO.builder().name(current.getAccountName()).asset(current.getTotal())
                        .net(current.getTotal() / current.getShare()).position(current.getPosition())
                        .gain(gain).build());
            }
        }
        //按照市值的大小进行排序
        accountVOS.sort((a, b) -> b.getAsset().compareTo(a.getAsset()));

        AccountVO baseVo = getBase(country, date);
        AccountVO indexVO = getIndex(country, date);
        accountVOS.add(0, indexVO);
        accountVOS.add(0, baseVo);
        for (AccountVO accountVO : accountVOS) {
            //不全收益差,补全等仓位跑赢
            accountVO.setGainDiff(accountVO.getGain() - baseVo.getGain());
            accountVO.setDate(dateStr);
            //计算等仓位跑赢
            if (baseVo.getPosition() != 0) {
                double acc = accountVO.getGain() / accountVO.getPosition();
                double ba = baseVo.getGain() / baseVo.getPosition();
                accountVO.setBeta((acc - ba) / (1 + ba));
            }
        }
        return accountVOS;
    }

    /**
     * 获取指数的最新涨跌幅
     *
     * @param country
     * @param date
     * @return
     */
    private AccountVO getIndex(String country, LocalDate date) {
        if ("CN".equals(country)) {
            //当前的指数信息
            IndexValue lastOne = indexValueService.getByDate(date);
            IndexValue before = indexValueService.getRecentlyByL(lastOne.getDate());
            double shCurrent = lastOne.getSh().doubleValue();
            double shBefore = before.getSh().doubleValue();
            return AccountVO.builder()
                    .asset(shCurrent)
                    .gain((shCurrent - shBefore) / shBefore).name("上证指数").position(1.0).build();
        } else {
            IndexUs lastOne = indexUSService.getByDate(date);
            IndexUs before = indexUSService.getByDate(date, "<");
            double shCurrent = lastOne.getSp500().doubleValue();
            double shBefore = before.getSp500().doubleValue();
            return AccountVO.builder()
                    .asset(shCurrent)
                    .gain((shCurrent - shBefore) / shBefore).name("标普500").position(1.0).build();
        }
    }

    /**
     * 获取 AI 系统的账户信息
     *
     * @param country
     * @return
     */
    private AccountVO getBase(String country, LocalDate date) {
        if ("CN".equals(country)) {
            Asset current = assetService.get(date);
            Asset before = assetService.getBeforeDate(date, "10000000");
            double balance = current.getBalance().doubleValue();
            double stock = current.getStock().doubleValue();
            double position = stock / (stock + balance);
            double beforeAsset = before.getBalance().add(before.getStock()).doubleValue();
            double currentAsset = current.getBalance().add(current.getStock()).doubleValue();
            return AccountVO.builder().position(position).net(currentAsset / 10000000).name("勾A股").asset(currentAsset)
                    .beta(0.0).gainDiff(0.0)
                    .gain((currentAsset - beforeAsset) / beforeAsset).build();
        } else {
            AssetUs current = assetUsService.getByUserAndDate("10000000", date);
            LocalDate beforeDate = assetUsService.getLDate(current.getDate());
            AssetUs Before = assetUsService.getByUserAndDate("10000000", beforeDate);
            double securityValue = current.getCover().abs().add(current.getStock()).doubleValue();
            double total = current.getCover().add(current.getBalance()).add(current.getStock()).doubleValue();
            double position = securityValue / total;
            double totalBefore = Before.getCover().add(Before.getBalance()).add(Before.getStock()).doubleValue();
            return AccountVO.builder().position(position).net(total / 10000000).name("勾美股").asset(total).beta(0.0).gainDiff(0.0)
                    .gain((total - totalBefore) / total).build();
        }
    }

    /**
     * 计算最大回撤
     *
     * @param price
     * @return
     */
    private BigDecimal calcMaxDd(List<BigDecimal> price) {
        BigDecimal max_unit_value = price.get(0);
        BigDecimal max_dd = BigDecimal.ZERO;
        BigDecimal dd;
        for (int i = 1; i < price.size(); i++) {
            max_unit_value = price.get(i).compareTo(max_unit_value) > 0 ? price.get(i) : max_unit_value;
            dd = price.get(i).divide(max_unit_value, 4, BigDecimal.ROUND_UP).subtract(new BigDecimal(1));
            max_dd = dd.compareTo(max_dd) < 0 ? dd : max_dd;
        }
        return max_dd.abs();
    }
}

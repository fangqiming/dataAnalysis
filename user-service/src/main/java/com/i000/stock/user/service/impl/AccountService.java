package com.i000.stock.user.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.bo.AccountBO;
import com.i000.stock.user.api.entity.vo.AccountCNRateVO;
import com.i000.stock.user.api.entity.vo.AccountUSRateVO;
import com.i000.stock.user.api.service.buiness.AssetService;
import com.i000.stock.user.api.service.original.IndexValueService;
import com.i000.stock.user.dao.bo.AccountAssetBO;
import com.i000.stock.user.dao.mapper.AccountAssetMapper;
import com.i000.stock.user.dao.mapper.AccountHoldMapper;
import com.i000.stock.user.dao.mapper.AccountMapMapper;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.us.service.AssetUsService;
import com.i000.stock.user.service.impl.us.service.IndexUSService;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private DateTimeFormatter DF = DateTimeFormatter.ofPattern("yy-MM-dd");

    @Resource
    private IndexValueService indexValueService;

    @Resource
    private AssetService assetService;

    @Resource
    private IndexUSService indexUSService;

    @Resource
    private AssetUsService assetUsService;

    @Resource
    private AccountAssetMapper accountAssetMapper;

    @Resource
    private AccountHoldMapper accountHoldMapper;

    @Resource
    private AccountMapMapper accountMapMapper;

    public void save(AccountBO accountBO) {
        AccountAsset accountAsset = accountBO.getAccountAsset();
        List<AccountHold> accountHoldList = accountBO.getAccountHoldList();
        if (Objects.nonNull(accountAsset)) {
            accountAssetMapper.insert(accountAsset);
        }
        if (!CollectionUtils.isEmpty(accountHoldList)) {
            for (AccountHold hold : accountHoldList) {
                accountHoldMapper.insert(hold);
            }
        }
    }

    /**
     * 将lqjj 转化为刘桥基金
     *
     * @param name
     * @return
     */
    public String getAccountName(String name) {
        EntityWrapper<AccountMap> ew = new EntityWrapper<>();
        ew.where("name={0}", name);
        List<AccountMap> accountMaps = accountMapMapper.selectList(ew);
        if (!CollectionUtils.isEmpty(accountMaps)) {
            return accountMaps.get(0).getAccount();
        }
        return name;
    }

    public AccountCNRateVO getCn(LocalDate start, LocalDate end) {
        AccountCNRateVO result = new AccountCNRateVO();
        Map<String, Map<LocalDate, Double>> account = getMap(start, end, "CN");
        Map<LocalDate, Double> index = getIndexCN(start, end);
        Map<LocalDate, Double> base = getAssetCN(start, end);
        Set<LocalDate> accountDate = account.get("lqjj").keySet();
        Set<LocalDate> baseDate = base.keySet();
        baseDate.retainAll(accountDate);
        baseDate.retainAll(index.keySet());
        //将set转化为List并排序
        Set<LocalDate> sortSet = new TreeSet<>(LocalDate::compareTo);
        sortSet.addAll(baseDate);
        //遍历,开始创建值
        for (LocalDate date : sortSet) {
            result.getDate().add(date.format(DF));
            result.getGag().add(base.get(date) * 100);
            result.getSzzs().add(index.get(date) * 100);
            result.getYczh().add(account.get("yczh").get(date) * 100);
            result.getLqjj().add(account.get("lqjj").get(date) * 100);
            result.getJhai().add(account.get("jhai").get(date) * 100);
        }
        return result;
    }


    public AccountUSRateVO getUS(LocalDate start, LocalDate end) {
        AccountUSRateVO result = new AccountUSRateVO();
        Map<String, Map<LocalDate, Double>> account = getMap(start, end, "US");
        Map<LocalDate, Double> index = getIndexUS(start, end);
        Map<LocalDate, Double> base = getAssetUS(start, end);
        Set<LocalDate> accountDate = account.get("xyzq").keySet();
        Set<LocalDate> baseDate = base.keySet();
        baseDate.retainAll(accountDate);
        baseDate.retainAll(index.keySet());
        //将set转化为List并排序
        Set<LocalDate> sortSet = new TreeSet<>(LocalDate::compareTo);
        sortSet.addAll(baseDate);
        //遍历,开始创建值
        for (LocalDate date : sortSet) {
            result.getDate().add(date.format(DF));
            result.getGmg().add(base.get(date) * 100);
            result.getSp().add(index.get(date) * 100);
            result.getXyzq().add(account.get("xyzq").get(date) * 100);
            result.getJxzq().add(account.get("jxzq").get(date) * 100);
            result.getLhzq().add(account.get("lhzq").get(date) * 100);
            result.getJxira().add(account.get("jxira").get(date) * 100);
        }
        return result;
    }

    /**
     * 获取账户在指定时间范围内的净值表现
     * Map<账户名, Map<日期, 净值>>
     *
     * @param start
     * @param end
     * @param country
     * @return
     */
    private Map<String, Map<LocalDate, Double>> getMap(LocalDate start, LocalDate end, String country) {
        Map<String, Map<LocalDate, Double>> result = new HashMap<>();
        List<AccountAssetBO> accounts = accountAssetMapper.findBetween(start, end, country);
        Map<String, List<AccountAssetBO>> accountMap =
                accounts.stream().collect(Collectors.groupingBy(AccountAssetBO::getAccountName));
        for (Map.Entry<String, List<AccountAssetBO>> entry : accountMap.entrySet()) {
            String key = entry.getKey();
            Map<LocalDate, Double> netMap = new HashMap<>();
            List<AccountAssetBO> value = entry.getValue();
            value.sort(Comparator.comparing(AccountAssetBO::getDate));
            AccountAssetBO base = value.get(0);
            double netBase = base.getTotal() / base.getShare();
            for (AccountAssetBO assetBO : value) {
                double netTmp = assetBO.getTotal() / assetBO.getShare();
                double gain = (netTmp - netBase) / Math.abs(netBase);
                netMap.put(assetBO.getDate(), gain);
            }
            result.put(key, netMap);
        }
        return result;
    }

    private Map<LocalDate, Double> getIndexCN(LocalDate start, LocalDate end) {
        Map<LocalDate, Double> result = new HashMap<>();
        List<IndexValue> between = indexValueService.findBetween(start, end);
        between.sort(Comparator.comparing(IndexValue::getDate));
        double base = between.get(0).getSh().doubleValue();
        for (IndexValue index : between) {
            double v = index.getSh().doubleValue();
            double gain = (v - base) / Math.abs(base);
            result.put(index.getDate(), gain);
        }
        return result;
    }

    private Map<LocalDate, Double> getIndexUS(LocalDate start, LocalDate end) {
        Map<LocalDate, Double> result = new HashMap<>();
        List<IndexUs> between = indexUSService.findBetweenDate(start, end);
        between.sort(Comparator.comparing(IndexUs::getDate));
        double base = between.get(0).getSp500().doubleValue();
        for (IndexUs index : between) {
            double v = index.getSp500().doubleValue();
            double gain = (v - base) / Math.abs(base);
            result.put(index.getDate(), gain);
        }
        return result;
    }

    private Map<LocalDate, Double> getAssetCN(LocalDate start, LocalDate end) {
        Map<LocalDate, Double> result = new HashMap<>();
        List<Asset> between = assetService.findAssetBetween("10000000", start, end);
        between.sort(Comparator.comparing(Asset::getDate));
        Asset base = between.get(0);
        Double baseAsset = base.getBalance().add(base.getStock()).doubleValue();
        for (Asset index : between) {
            double v = index.getBalance().add(index.getStock()).doubleValue();
            double gain = (v - baseAsset) / Math.abs(baseAsset);
            result.put(index.getDate(), gain);
        }
        return result;
    }

    private Map<LocalDate, Double> getAssetUS(LocalDate start, LocalDate end) {
        Map<LocalDate, Double> result = new HashMap<>();
        List<AssetUs> between = assetUsService.findBetweenDateByUser(start, end, "10000000");
        between.sort(Comparator.comparing(AssetUs::getDate));
        AssetUs base = between.get(0);
        Double baseAsset = base.getBalance().add(base.getStock()).add(base.getCover()).doubleValue();
        for (AssetUs index : between) {
            double v = index.getBalance().add(index.getStock()).add(index.getCover()).doubleValue();
            double gain = (v - baseAsset) / Math.abs(baseAsset);
            result.put(index.getDate(), gain);
        }
        return result;
    }
}

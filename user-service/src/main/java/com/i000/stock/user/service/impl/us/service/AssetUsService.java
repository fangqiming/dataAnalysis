package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.core.util.TimeUtil;
import com.i000.stock.user.dao.mapper.AssetUsMapper;
import com.i000.stock.user.dao.model.AssetUs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class AssetUsService {

    @Autowired
    private AssetUsMapper assetUsMapper;

    public AssetUs getNewest(String user) {
        EntityWrapper<AssetUs> ew = new EntityWrapper<>();
        ew.where("user={0}", user).orderBy("date", false).last("LIMIT 1");
        return getOne(ew);
    }

    public LocalDate getLDate(LocalDate date) {
        return assetUsMapper.getLD(date);
    }

    public List<AssetUs> findNewestTwoByUser(String user) {
        EntityWrapper<AssetUs> ew = new EntityWrapper<>();
        ew.where("user={0}", user)
                .orderBy("id", false).last("limit 2");
        return assetUsMapper.selectList(ew);
    }


    public AssetUs getByUserAndDate(String user, LocalDate date) {
        LocalDate before = LocalDate.parse("2019-02-01", TimeUtil.DF);
        if (date.compareTo(before) < 0) {
            date = before;
        }
        EntityWrapper<AssetUs> ew = new EntityWrapper<>();
        ew.where("user={0}", user)
                .and("date={0}", date);
        return getOne(ew);
    }

    public BigDecimal getAvgPositionByUser(String user) {
        return assetUsMapper.getAvgPositionByUser(user);
    }

    public AssetUs getLtDateByDateAndUser(LocalDate date, String user) {
        EntityWrapper<AssetUs> ew = new EntityWrapper<>();
        ew.where("date<{0}", date)
                .and("user={0}", user)
                .orderBy("date", false)
                .last("limit 1");
        return getOne(ew);
    }

    public List<AssetUs> findBetweenDateByUser(LocalDate start, LocalDate end, String user) {
        EntityWrapper<AssetUs> ew = new EntityWrapper<>();
        ew.where("user={0}", user).and()
                .between("date", start, end);
        return assetUsMapper.selectList(ew);
    }

    public AssetUs getOldestOneByUser(String user) {
        EntityWrapper<AssetUs> ew = new EntityWrapper<>();
        ew.where("user={0}", user).orderBy("date", true).last("limit 1");
        return getOne(ew);
    }

    public void insert(AssetUs assetUs) {
        assetUsMapper.insert(assetUs);
    }

    private AssetUs getOne(EntityWrapper<AssetUs> ew) {
        List<AssetUs> assetUses = assetUsMapper.selectList(ew);
        if (CollectionUtils.isEmpty(assetUses)) {
            return null;
        }
        return assetUses.get(0);
    }


}

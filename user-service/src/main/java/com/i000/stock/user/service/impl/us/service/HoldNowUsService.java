package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.api.entity.bo.ShareSplitUpBO;
import com.i000.stock.user.dao.mapper.HoldNowUsMapper;
import com.i000.stock.user.dao.model.HoldNowUs;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class HoldNowUsService {

    @Autowired
    private HoldNowUsMapper holdNowUsMapper;

    public List<HoldNowUs> findByUser(@Param("user") String user) {
        EntityWrapper<HoldNowUs> ew = new EntityWrapper<>();
        ew.where("user = {0}", user);
        return holdNowUsMapper.selectList(ew);
    }

    public void handleShareSplitUp(ShareSplitUpBO shareSplitUpBO) {
        holdNowUsMapper.handleShareSplitUp(shareSplitUpBO.getNewPrice(), shareSplitUpBO.getCode());
    }

    public void updateSharePriceAndDate(String code, BigDecimal price, LocalDate date) {
        holdNowUsMapper.updateSharePriceAndDate(price, date, code);
    }

    public BigDecimal getHoldLongCount() {
        return holdNowUsMapper.getHoldLongCount();
    }

    public BigDecimal getHoldCount() {
        return holdNowUsMapper.getHoldCount();
    }

    public List<HoldNowUs> findByCode(String code) {
        EntityWrapper<HoldNowUs> ew = new EntityWrapper<>();
        ew.where("code={0}", code);
        return holdNowUsMapper.selectList(ew);
    }

    public void deleteBatchIds(List<Long> ids) {
        holdNowUsMapper.deleteBatchIds(ids);
    }

    public void insert(HoldNowUs holdNowUs) {
        holdNowUsMapper.insert(holdNowUs);
    }

    public BigDecimal getStock(String user) {
        BigDecimal stock = holdNowUsMapper.getStock(user);
        if (Objects.isNull(stock)) {
            stock = BigDecimal.ZERO;
        }
        return stock;
    }

    public BigDecimal getCover(String user) {
        BigDecimal stock = holdNowUsMapper.getCover(user);
        if (Objects.isNull(stock)) {
            stock = BigDecimal.ZERO;
        }
        return stock;
    }


}

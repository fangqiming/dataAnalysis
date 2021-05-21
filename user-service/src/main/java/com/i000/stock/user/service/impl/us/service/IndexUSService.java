package com.i000.stock.user.service.impl.us.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.i000.stock.user.core.util.TimeUtil;
import com.i000.stock.user.dao.mapper.IndexUsMapper;
import com.i000.stock.user.dao.model.AssetUs;
import com.i000.stock.user.dao.model.IndexUs;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 美股的指数信息获取服务
 * 最好也是提前做好缓存
 */
@Log4j
@Service
public class IndexUSService {

    @Autowired
    private ExternalServiceImpl externalService;

    @Autowired
    private IndexUsMapper indexUsMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private AssetUsService assetUsService;

    private final String INDEX_US = "list=int_dji,int_nasdaq,int_sp500";

    public IndexUs getNewestFromNet() {
        log.warn("调取美股指数");
        String origin = restTemplate.getForEntity("http://hq.sinajs.cn/list=int_dji,int_nasdaq,int_sp500", String.class).getBody();
        System.out.println(origin);
        IndexUs result = new IndexUs();
        if (!StringUtils.isEmpty(origin)) {
            String[] split = origin.split(";");
            for (String str : split) {
                if (str.contains("int_dji")) {
                    result.setDji(getValue(str));
                } else if (str.contains("int_nasdaq")) {
                    result.setNasdaq(getValue(str));
                } else if (str.contains("int_sp500")) {
                    result.setSp500(getValue(str));
                }
            }
        }
        //日期不能够这样获取.因为获取到的最新日期,现在需要获取到美股最新的交易日期才行.
        //此日期在文档里面有
        AssetUs newest = assetUsService.getNewest("10000000");
        result.setDate(newest.getDate());
        return result;
    }

    public List<IndexUs> findTwoNewest() {
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        ew.orderBy("date", false).last("limit 2");
        return indexUsMapper.selectList(ew);
    }

    public void insert(IndexUs indexUs) {
        indexUsMapper.insert(indexUs);
    }

    public IndexUs getOldestFromDB() {
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        ew.orderBy("id", true).last("limit 1");
        return getOne(ew);
    }

    public IndexUs getNewestFromDB() {
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        ew.orderBy("id", false).last("limit 1");
        return getOne(ew);
    }

    public IndexUs getLtDateOne(LocalDate date) {
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        ew.where("date<{0}", date).orderBy("date", false).last("limit 1");
        return getOne(ew);
    }

    public IndexUs getByDate(LocalDate date) {
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        ew.where("date = {0}", date);
        return getOne(ew);
    }

    public List<IndexUs> findBetweenDate(LocalDate start, LocalDate end) {
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        ew.between("date", start, end);
        return indexUsMapper.selectList(ew);
    }


    private BigDecimal getValue(String str) {
        String value = str.split("=")[1];
        return new BigDecimal(value.split(",")[1]);
    }

    private LocalDate getDate(String str) {
        String dateStr = str.split(",")[3];
        return TimeUtil.getNYTimeByBJTime(dateStr).toLocalDate();
    }

    private IndexUs getOne(EntityWrapper<IndexUs> ew) {
        List<IndexUs> indexUses = indexUsMapper.selectList(ew);
        if (CollectionUtils.isEmpty(indexUses)) {
            return null;
        }
        return indexUses.get(0);
    }

    public List<IndexUs> findByDate(List<LocalDate> dates) {
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        ew.in("date", dates);
        return indexUsMapper.selectList(ew);
    }

    public IndexUs getByDate(LocalDate date, String operate) {
        LocalDate before = LocalDate.parse("2019-02-01", TimeUtil.DF);
        if (date.compareTo(before) < 0) {
            date = before;
        }
        EntityWrapper<IndexUs> ew = new EntityWrapper<>();
        String param = String.format("date%s{0}", operate);
        ew.where(param, date).orderBy("date", false).last("limit 1");
        List<IndexUs> indexUses = indexUsMapper.selectList(ew);
        if (CollectionUtils.isEmpty(indexUses)) {
            return null;
        }
        return indexUses.get(0);
    }


}

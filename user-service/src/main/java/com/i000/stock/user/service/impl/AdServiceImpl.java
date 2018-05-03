package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.AdService;
import com.i000.stock.user.dao.mapper.AdMapper;
import com.i000.stock.user.dao.model.Ad;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 16:16 2018/4/28
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class AdServiceImpl implements AdService {

    @Resource
    private AdMapper adMapper;

    @Override
    public String get(String key) {
        Ad ad = adMapper.get(key);
        return Objects.isNull(ad) ? "" : ad.getValue();
    }

    @Override
    public void save(Ad ad) {
        if (Objects.nonNull(ad)) {
            if (StringUtils.isBlank(get(ad.getKey()))) {
                adMapper.insert(ad);
            } else {
                adMapper.updateByKey(ad);
            }
        }
    }
}

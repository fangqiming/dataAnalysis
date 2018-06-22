package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.entity.bo.IndexBo;
import com.i000.stock.user.api.entity.bo.IndexInfo;
import com.i000.stock.user.api.service.EmailService;
import com.i000.stock.user.api.service.IndexService;
import com.i000.stock.user.service.impl.external.ExternalServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 19:16 2018/4/25
 * @Modified By:
 */
@Slf4j
@Component
@Transactional
public class IndexServiceImpl implements IndexService {

    @Autowired
    private ExternalServiceImpl externalService;

    @Resource
    private EmailService emailService;

    @Override
    public List<IndexInfo> get() {
        IndexBo index = getIndexBo();
        if (!Objects.isNull(index)) {
            return index.getData();
        }
        return new ArrayList<>(0);
    }


    private IndexBo getIndexBo() {
        for (int i = 0; i < 5; i++) {
            try {
                IndexBo index = externalService.getIndex();
                if (Objects.nonNull(index)) {
                    return index;
                }
            } catch (Exception e) {
                sleep(3000L);
            }

        }
        emailService.sendMail("【千古：获取指数的接口异常】", "指数接口重试超过5次仍旧异常请确认网络是否正常", true);
        return null;
    }

    private void sleep(Long second) {
        try {
            Thread.sleep(second);
        } catch (InterruptedException e) {
            log.debug("重试发生中断异常");
        }
    }
}

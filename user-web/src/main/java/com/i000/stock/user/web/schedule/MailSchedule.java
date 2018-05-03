package com.i000.stock.user.web.schedule;

import com.i000.stock.user.api.service.AssetService;
import com.i000.stock.user.api.service.HoldService;
import com.i000.stock.user.api.service.MailFetchService;
import com.i000.stock.user.api.service.UserInfoService;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.Hold;
import com.i000.stock.user.dao.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 17:26 2018/4/27
 * @Modified By:
 */
@Slf4j
@Component
public class MailSchedule {

    @Resource
    private MailFetchService mailFetchService;

    @Resource
    private AssetService assetService;

    @Resource
    private HoldService holdService;

    @Resource
    private UserInfoService userInfoService;

    @Scheduled(cron = "0 35 15 * * ?")
    public void fetchMail() throws Exception {
        LocalDate localDate = mailFetchService.initMail();
        System.out.println("开始时间:" + System.currentTimeMillis());
        List<Hold> trade = holdService.getTrade();
        if (Objects.nonNull(localDate)) {
            Page<UserInfo> search = userInfoService.search(BaseSearchVo.builder().pageNo(1).pageSize(50).build());
            double ceil = Math.ceil(search.getTotal() / 50.0);
            calculate(search, localDate, trade);
            for (int i = 2; i <= ceil; i++) {
                Page<UserInfo> page = userInfoService.search(BaseSearchVo.builder().pageNo(i).pageSize(50).build());
                calculate(page, localDate, trade);
            }
        }
        System.out.println("结束时间:" + System.currentTimeMillis());
    }


    private void calculate(Page<UserInfo> page, LocalDate date, List<Hold> trade) {
        if (!CollectionUtils.isEmpty(page.getList())) {
            for (UserInfo userInfo : page.getList()) {
                assetService.calculate(date, userInfo.getName(), trade);
            }
        }
    }
}

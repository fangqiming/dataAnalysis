package com.i000.stock.user.web.controller;

import com.i000.stock.user.api.service.*;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.core.util.ValidationUtils;
import com.i000.stock.user.dao.bo.BaseSearchVo;
import com.i000.stock.user.dao.bo.Page;
import com.i000.stock.user.dao.model.Hold;
import com.i000.stock.user.dao.model.UserInfo;
import com.i000.stock.user.service.impl.RecommendParseImpl;
import com.i000.stock.user.web.config.MailSendConfig;
import com.i000.stock.user.web.thread.ReceiveRecommendThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:03 2018/5/8
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/engine")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EngineController {

    @Autowired
    private RecommendParseImpl recommendParse;

    @Resource
    private AssetService assetService;

    @Resource
    private HoldService holdService;

    @Resource
    private UserInfoService userInfoService;

    @Autowired
    private ReceiveRecommendThread receiveRecommendThread;

    @Resource
    private FileService fileService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MailSendConfig mailSendConfig;


    /**
     * 用于接收推荐信息 注意没有考虑拆股与分红
     *
     * @return
     */
    @PostMapping(value = "/receive_recommend")
    public ResultEntity receiveRecommend(@RequestBody String content, @RequestParam(defaultValue = "1") Integer needSave) {
        ValidationUtils.validateParameter(content, "内容不能为空");
        if (needSave > 0) {
            fileService.saveFile(content);
        }

        receiveRecommendThread.execute(() -> {
            try {
                //获取了推荐日期 保存了原始数据
                LocalDate date = recommendParse.parse(content);

                //通过今天的持股和上一次的持股做对比获取到今天的交易信息
                List<Hold> trade = holdService.getTrade();

                //根据推荐日期获取到现在的持股情况
                List<Hold> holdInit = holdService.findHoldInit(date);
                if (Objects.nonNull(date)) {
                    //分页获取用户
                    Page<UserInfo> search = userInfoService.search(BaseSearchVo.builder().pageNo(1).pageSize(50).build());
                    double ceil = Math.ceil(search.getTotal() / 50.0);
                    //开始计算每一个用户的收益，同时记录用户交易记录，持股记录
                    calculate(search, date, trade, holdInit);
                    for (int i = 2; i <= ceil; i++) {
                        Page<UserInfo> page = userInfoService.search(BaseSearchVo.builder().pageNo(i).pageSize(50).build());
                        calculate(page, date, trade, holdInit);
                    }
                }
                if (needSave > 0) {
                    emailService.sendMail("【千古:数据解析成功】", content, mailSendConfig.isSendSuccessNotice());
                }
            } catch (Exception e) {
                log.error("[DATA PARES ERROR] e=[{}]", e);
                StringBuffer email = new StringBuffer(content);
                email.append("\r\n\r\n\r\n\r\n\r\n");
                email.append("-------------EXCEPTION INFO----------------\r\n");
                email.append(e);
                emailService.sendMail("【千古:推荐数据解析错误-相关人员请立即查看】", email.toString(), mailSendConfig.isSendSuccessNotice());
            }
        });
        return Results.newNormalResultEntity("result", "success");
    }


    private void calculate(Page<UserInfo> page, LocalDate date, List<Hold> trade, List<Hold> holdInit) {
        if (!CollectionUtils.isEmpty(page.getList())) {
            for (UserInfo userInfo : page.getList()) {
                assetService.calculate(date, userInfo.getName(), trade, holdInit);
            }
        }
    }


}

package com.i000.stock.user.service.impl.us;

import com.i000.stock.user.api.entity.bo.ShareSplitUpBO;
import com.i000.stock.user.api.service.util.EmailService;
import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.exception.ServiceException;
import com.i000.stock.user.dao.model.*;
import com.i000.stock.user.service.impl.us.parse.Parse;
import com.i000.stock.user.service.impl.us.parse.ParseHandle;
import com.i000.stock.user.service.impl.us.service.*;
import com.i000.stock.user.service.impl.us.trade.FetchTradeHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class ParseReportService {

    @Autowired
    private AssetUsService assetUsService;

    @Autowired
    private ParseHandle parseHandle;

    @Autowired
    private PlanUsService planUsService;

    @Autowired
    private TradeDetailUsService tradeDetailUsService;

    @Autowired
    private HoldUsService holdUsService;

    @Autowired
    private HoldNowUsService holdNowUsService;

    @Autowired
    private TradeUsService tradeUsService;

    @Autowired
    private FetchTradeHandle fetchTradeHandle;

    @Autowired
    private UserInfoUsService userInfoUsService;

    @Autowired
    private EmailService emailService;

    private LocalDate parseContentToDB(String content) {
        //将报告切成一块一块
        String[] sections = content.split(PatternUtil.SECTION.pattern());
        LocalDate date = getDate(sections);
        if (planUsService.hasDate(date)) {
            throw new ServiceException(ApplicationErrorMessage.US_REPORT_HAD_PARSE);
        }
        //还需要判定是否能够进行存储。
        //再将每一块切成一段一段的，同时需要能够明确分别属于哪一部分的片段
        for (String section : sections) {
            String[] lines = section.split(PatternUtil.LINE.pattern());
            if (lines.length > 0) {
                String key = lines[0].replaceAll("[0-9]{8}", "HH");
                Parse parse = parseHandle.get(key);
                if (Objects.nonNull(parse)) {
                    parse.save(lines, date);
                }
            }
        }
        return date;
    }

    public void parse(String content) {
        //报告已经解析到数据库
        LocalDate date = parseContentToDB(content);
        try {
            //获取到最新的持仓
            List<HoldUs> holds = holdUsService.findByDate(date);
            //获取到最新的交易详情
            List<TradeDetailUs> tradeDetails = tradeDetailUsService.findByDate(date);
            //获取到今天的交易记录
            List<TradeUs> todayTrade = tradeUsService.findByDate(date);
            //获取到全部的发生了拆并股的股票
            List<ShareSplitUpBO> shareSplitUpBOS = findShareSplitUp(holds, tradeDetails);
            //对发生了拆并股的股票数据进行修改
            handleShareSplitUp(shareSplitUpBOS);
            //更新持仓股票的价格
            updateHoldSharePriceAndDate(holds, todayTrade, date);
            //根据今日的交易记录交易股票(一卖就是都卖了)

            //获取到用户信息，然后根据用户信息在进行交易，不需要选择金额了
            String user = "10000000";
            UserInfoUs userInfoUs = userInfoUsService.getByUser(user);
            AssetUs asset = getNewestAsset(user, userInfoUs);
            AssetUs assetUs = tradeShare(todayTrade, userInfoUs, asset);
            AssetUs assetResult = insetAsset(assetUs, date);
            //更新报告
            updatePlan(userInfoUs, assetResult);
            emailService.sendMail("【毕达:美股数据解析成功】", content, true);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            StringBuffer email = new StringBuffer(content);
            email.append("\r\n\r\n\r\n\r\n\r\n");
            email.append("-------------EXCEPTION INFO----------------\r\n");
            email.append(e);
            emailService.sendMail("【毕达:美股Report解析错误】", email.toString(), true);

        }

    }

    private AssetUs insetAsset(AssetUs assetUs, LocalDate date) {
        assetUs.setCover(holdNowUsService.getCover(assetUs.getUser()));
        assetUs.setStock(holdNowUsService.getStock(assetUs.getUser()));
        assetUs.setDate(date);
        assetUs.setId(null);
        assetUsService.insert(assetUs);
        return assetUs;
    }

    private void updatePlan(UserInfoUs userInfoUs, AssetUs assetUs) {
        //获取plan
        List<PlanUs> recommend = planUsService.findRecommend();
        if (!CollectionUtils.isEmpty(recommend)) {
            BigDecimal oneShareMoney = getOneShareMoney(userInfoUs, assetUs);
            BigDecimal rate = oneShareMoney.divide(assetUs.getBalance().add(assetUs.getCover())
                    .add(assetUs.getStock()), 4, BigDecimal.ROUND_UP).multiply(new BigDecimal(100));
            for (PlanUs planUs : recommend) {
                if ("BUY".equals(planUs.getAction()) || "SHORT".equals(planUs.getAction())) {
                    planUs.setQuantity(oneShareMoney);
                    planUs.setRate(rate);
                } else {
                    planUs.setRate(new BigDecimal("100"));
                }
                planUsService.updateById(planUs);
            }
        }
    }

    private void handleShareSplitUp(List<ShareSplitUpBO> shareSplitUpBOS) {
        if (!CollectionUtils.isEmpty(shareSplitUpBOS)) {
            for (ShareSplitUpBO shareSplitUpBO : shareSplitUpBOS) {
                holdNowUsService.handleShareSplitUp(shareSplitUpBO);
            }
        }
    }

    /**
     * 获取到存在拆并股的股票
     */
    private List<ShareSplitUpBO> findShareSplitUp(List<HoldUs> holds, List<TradeDetailUs> tradeDetails) {
        List<ShareSplitUpBO> result = new ArrayList<>();
        //获取到今天的持仓

        for (HoldUs holdUs : holds) {
            ShareSplitUpBO shareCode = createShareCode(holdUs.getCode(),
                    holdUs.getOldDate(), holdUs.getOldPrice());
            if (Objects.nonNull(shareCode)) {
                result.add(shareCode);
            }
        }

        //根据交易详情来处理拆并股是对应SHORT的股票需要做特殊的处理

        List<TradeDetailUs> longType = tradeDetails.stream()
                .filter(a -> !a.getType().equals("SHORT")).collect(Collectors.toList());
        for (TradeDetailUs tradeDetailUs : longType) {
            ShareSplitUpBO shareCode = createShareCode(tradeDetailUs.getCode(),
                    tradeDetailUs.getOldDate(), tradeDetailUs.getOldPrice());
            if (Objects.nonNull(shareCode)) {
                result.add(shareCode);
            }
        }

        List<TradeDetailUs> shortType = tradeDetails.stream()
                .filter(a -> a.getType().equals("SHORT")).collect(Collectors.toList());
        for (TradeDetailUs tradeDetailUs : shortType) {
            ShareSplitUpBO shareCode = createShareCode(tradeDetailUs.getCode(),
                    tradeDetailUs.getOldDate(), tradeDetailUs.getOldPrice());

            if (Objects.nonNull(shareCode)) {
                result.add(shareCode);
            }
        }


        return result;
    }

    private ShareSplitUpBO createShareCode(String code, LocalDate date, BigDecimal newPrice) {

        BigDecimal cost = holdUsService.getOldPriceByDateAndCode(date, code);
        if (cost.compareTo(newPrice) != 0) {
            return ShareSplitUpBO.builder().code(code).newPrice(newPrice).oldPrice(cost).build();
        }
        return null;
    }

    private LocalDate getDate(String[] sections) {
        for (String section : sections) {
            String[] lines = section.split(PatternUtil.LINE.pattern());
            if (lines.length > 0) {
                if (PatternUtil.LONG1_HOLD.matcher(lines[0]).find()) {
                    String str = lines[0].split(" , ")[0];
                    String dateStr = str.split(" ")[3];
                    return LocalDate.parse(dateStr, PatternUtil.DF);
                } else if (lines[0].contains("Todays Trades")) {
                    String str = lines[1].split("trade_desc_")[1];
                    String dateStr = str.split("\\.")[0];
                    return LocalDate.parse(dateStr, PatternUtil.DF);
                }
            }
        }
        throw new ServiceException(ApplicationErrorMessage.US_PARSE_ERROR);
    }

    private void updateHoldSharePriceAndDate(List<HoldUs> holds, List<TradeUs> todayTrade, LocalDate date) {
        //更新股票的价格
        for (HoldUs holdUs : holds) {
            holdNowUsService.updateSharePriceAndDate(holdUs.getCode(), holdUs.getNewPrice(), date);
        }
        List<TradeUs> eveningUp = todayTrade.stream()
                .filter(a -> a.getAction().equals("SELL") || a.getAction().equals("COVER"))
                .collect(Collectors.toList());
        for (TradeUs tradeUs : eveningUp) {
            holdNowUsService.updateSharePriceAndDate(tradeUs.getCode(), tradeUs.getPrice(), date);
        }
    }

    private AssetUs tradeShare(List<TradeUs> todayTrade, UserInfoUs userInfoUs, AssetUs assetUs) {
        //在交易前获取到每一单应该买入的数量
        BigDecimal oneShareMoney = getOneShareMoney(userInfoUs, assetUs);
        //开始交易
        for (TradeUs tradeUs : todayTrade) {
            assetUs = fetchTradeHandle.getTrade(tradeUs.getAction()).trade(assetUs, tradeUs, oneShareMoney);
        }
        return assetUs;
    }

    private AssetUs getNewestAsset(String user, UserInfoUs userInfoUs) {
        AssetUs assetUs = assetUsService.getNewest(user);
        if (Objects.isNull(assetUs)) {
            assetUs = AssetUs.builder().cover(BigDecimal.ZERO).balance(userInfoUs.getAmount())
                    .date(userInfoUs.getDate()).stock(BigDecimal.ZERO).cover(BigDecimal.ZERO).user(user).build();
            assetUsService.insert(assetUs);
        }
        return assetUs;
    }

    /**
     * todo 重点检查买入的金额是否正确
     *
     * @param userInfoUs
     * @param assetUs
     * @return
     */
    private BigDecimal getOneShareMoney(UserInfoUs userInfoUs, AssetUs assetUs) {
            return (assetUs.getStock().add(assetUs.getBalance()).add(assetUs.getCover()))
                    .divide(new BigDecimal(userInfoUs.getShare()), 0, BigDecimal.ROUND_UP);
    }
}

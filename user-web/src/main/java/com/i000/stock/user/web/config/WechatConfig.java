package com.i000.stock.user.web.config;

import cn.zhouyafeng.itchat4j.api.MessageTools;
import cn.zhouyafeng.itchat4j.api.WechatTools;
import cn.zhouyafeng.itchat4j.beans.BaseMsg;
import cn.zhouyafeng.itchat4j.face.IMsgHandlerFace;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.web.controller.RecommendController;
import com.i000.stock.user.web.controller.TradeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

/**
 * 代码比较耦合，可以进行分离。。
 */
@Component
public class WechatConfig implements ApplicationRunner {

    @Value("${path.wechat}")
    private String path;

    private String groupId = null;

    private DecimalFormat fnum = new DecimalFormat("##0.00");

    @Autowired
    private TradeController tradeController;

    @Autowired
    private RecommendController recommendController;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
//        System.out.println("。。。。。。。微信启动。。。。。。。path=" + path);
//        Wechat wechat = new Wechat(new MsgHandler(), path);
//        wechat.start();
    }

    private StringBuffer createProfitMsg() {
        ResultEntity assetSummary = tradeController.getAssetSummary();
        JSONObject asset = (JSONObject) JSON.toJSON(assetSummary);
        JSONObject today = asset.getJSONObject("data").getJSONObject("entity").getJSONObject("todayAccountBo");
        String todayProfit = fnum.format(today.getFloat("relativeProfitRate") * 100) + "%";
        String debt = fnum.format(today.getFloat("beatStandardRate")) + "%";
        String position = fnum.format(today.getFloat("position")) + "%";
        String todayMsg = "%s公告：\n" +
                "当天收益率：      %s\n" +
                "跑赢上证：        %s\n" +
                "当前仓位：        %s\n" +
                "-----------------------\n";
        StringBuffer todayStr = new StringBuffer(String.format(todayMsg, today.getString("date"), todayProfit, debt, position));

        String totalMsg = "累计收益公告：\n" +
                "累计收益率:          %s\n" +
                "跑赢上证：           %s\n" +
                "平均仓位：           %s\n" +
                "------------------------\n";

        JSONObject total = asset.getJSONObject("data").getJSONObject("entity").getJSONObject("totalAccountBo");
        String totalProfit = fnum.format(total.getFloat("relativeProfitRate") * 100) + "%";
        String totalDebt = fnum.format(total.getFloat("beatStandardRate")) + "%";
        String totalPosition = fnum.format(total.getFloat("avgPosition")) + "%";
        StringBuffer totalStr = new StringBuffer(String.format(totalMsg, totalProfit, totalDebt, totalPosition));
        return todayStr.append(totalStr);
    }

    private StringBuffer createWeekProfit() {
        String weekStr = "近一周公告：\n" +
                "收益率：           %s\n" +
                "跑赢上证：         %s\n" +
                "------------------------\n";
        JSONObject profit = (JSONObject) JSON.toJSON(tradeController.findProfit(""));
        JSONArray weekGain = profit.getJSONObject("data").getJSONArray("entities")
                .getJSONObject(0).getJSONArray("gain");
        String week = fnum.format(weekGain.getJSONObject(0).getFloat("profit")) + "%";
        String debt = fnum.format(weekGain.getJSONObject(4).getFloat("profit")) + "%";
        return new StringBuffer(String.format(weekStr, week, debt));
    }

    private StringBuffer createWinRate() {
        String winStr = "当前胜率：         %s\n" +
                "------------------------\n";
        JSONObject operator = (JSONObject) JSON.toJSON(tradeController.getOperator());
        String winRate = fnum.format(operator.getJSONObject("data").getJSONObject("entity").getFloat("winRate")) + "%";
        return new StringBuffer(String.format(winStr, winRate));
    }


    private StringBuffer createPlan() {
        String planStr = "明日推荐:\n";
        ResultEntity resultEntity = recommendController.find();
        JSONObject plan = (JSONObject) JSON.toJSON(resultEntity);
        JSONArray plans = plan.getJSONObject("data").getJSONArray("entities");
        for (int i = 0; i < plans.size(); i++) {
            JSONObject temp = plans.getJSONObject(i);
            if (!temp.getString("name").equals("204001")) {
                if (!temp.getString("action").equals("卖出")) {
                    planStr += "卖出 全部金额的 " + temp.getString("name") + "\n";
                } else {
                    planStr += "买入 总金额的 12.5%" + temp.getString("name") + "\n";
                }
            }
        }
        planStr += "剩余可用资金进行逆回购";
        return new StringBuffer(planStr);
    }

    public void sendRecommendMsg() {
        StringBuffer asset = createProfitMsg();
        StringBuffer week = createWeekProfit();
        StringBuffer winRate = createWinRate();
        StringBuffer plan = createPlan();
        StringBuffer result = asset.append(week).append(winRate).append(plan);
        System.out.println("消息即将发送：" + result);
        sendMsg(result.toString());
    }


    private void sendMsg(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            setGroupId();
            System.out.println("组Id为：" + groupId);
            MessageTools.sendMsgById(msg, groupId);
        }
    }


    private void setGroupId() {
        if (StringUtils.isEmpty(groupId)) {
            List<String> groupIdList = WechatTools.getGroupIdList();
            for (String id : groupIdList) {
                JSONArray memberListByGroupId = WechatTools.getMemberListByGroupId(id);
                if (Objects.nonNull(memberListByGroupId)) {
                    for (Object o : memberListByGroupId) {
                        JSONObject temp = (JSONObject) o;
                        if (temp.get("NickName").equals("毕达科技")) {
                            groupId = id;
                            break;
                        }
                    }
                }
            }
        }
    }

    private class MsgHandler implements IMsgHandlerFace {

        @Override
        public String textMsgHandle(BaseMsg msg) {
            return null;
        }

        @Override
        public String picMsgHandle(BaseMsg msg) {
            return null;
        }

        @Override
        public String voiceMsgHandle(BaseMsg msg) {
            return null;
        }

        @Override
        public String viedoMsgHandle(BaseMsg msg) {
            return null;
        }

        @Override
        public String nameCardMsgHandle(BaseMsg msg) {
            return null;
        }

        @Override
        public void sysMsgHandle(BaseMsg msg) {

        }

        @Override
        public String verifyAddFriendMsgHandle(BaseMsg msg) {
            return null;
        }

        @Override
        public String mediaMsgHandle(BaseMsg msg) {
            return null;
        }
    }
}

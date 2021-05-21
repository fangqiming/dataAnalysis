package com.i000.stock.user.web.service.account;

import com.i000.stock.user.api.entity.bo.AccountBO;
import com.i000.stock.user.dao.model.AccountAsset;
import com.i000.stock.user.dao.model.AccountHold;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Ib implements IPicture {

    @Resource
    private OcrClient ocrClient;

    @Override
    public AccountBO parse(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        String context = getContext(url, ocrClient);
        AccountAsset asset = getAsset(url);
        asset.setCountry("US");
        context = context.replaceAll(",", "");
        Double total = Double.valueOf(context.split("USD\\)\t")[1].split(TAB)[0]);
        asset.setTotal(total);
        //创建持仓列表
        List<AccountHold> accountHolds = new ArrayList<>();
        String holdStr = context.split("平均价格\t")[1];
        //将所有的股票列表全部放到列表中
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(holdStr);
        List<String> symbols = new ArrayList<>();
        while (matcher.find()) {
            symbols.add(matcher.group());
        }

        String[] holdArray = holdStr.split("[a-zA-Z]+");
        List<String> holdList = new ArrayList<>();
        for (String str : holdArray) {
            if (!StringUtils.isEmpty(str)) {
                holdList.add(str);
            }
        }

        for (int i = 0; i < holdList.size(); i++) {
            String s = holdList.get(i);
            String[] lines = s.split(TAB);
            if (lines.length > 4) {
                AccountHold accountHold = new AccountHold();
                accountHold.setCode(symbols.get(i));
                accountHold.setCost(getDouble(lines[4]));
                accountHold.setPrice(getDouble(lines[3]));
                accountHold.setQuantity(getInt(lines[1]));
                accountHold.setDate(asset.getDate());
                accountHold.setAccountName(asset.getAccountName());
                accountHolds.add(accountHold);
            }
        }

        result.setAccountAsset(asset);
        result.setAccountHoldList(accountHolds);
        return result;
    }
}

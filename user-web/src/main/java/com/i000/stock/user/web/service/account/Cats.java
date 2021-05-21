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
public class Cats implements IPicture {

    @Resource
    private OcrClient ocrClient;


    @Override
    public AccountBO parse(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        AccountAsset accountAsset = getAsset(url);
        accountAsset.setCountry("CN");
        String context = getContext(url, ocrClient);
        context = context.replaceAll(" ", "\t");
        System.out.println(context);
        //总资产中,信用账户与普通账户的位置不一样
//        Double total = Double.valueOf(context.split("\\d{10,12}\t")[1].split(TAB)[1].replaceAll(",", "").replaceAll(" ", ""));
        if ("1".equals(accountAsset.getType())) {
            //普通账户
            String[] account = context.split("账户.{1,2}总")[1].split(TAB);
            List<String> accountList = new ArrayList<>();
            for (String str : account) {
                if (!StringUtils.isEmpty(str)) {
                    accountList.add(str);
                }
            }
            accountAsset.setTotal(getDouble(accountList.get(5)));
        }
        if ("2".equals(accountAsset.getType())) {
            String total = context.split("\\d{10,12}\t")[1].split(TAB)[1];
            accountAsset.setTotal(getDouble(total));
        }

        List<AccountHold> accountHolds = new ArrayList<>();

        if (context.contains(")")) {
            Pattern pattern = Pattern.compile("[\\u4E00-\\u9FA5]+");
            String holdStr = context.split("\\)\t")[1];
            Matcher matcher = pattern.matcher(holdStr);
            List<String> symbols = new ArrayList<>();
            while (matcher.find()) {
                symbols.add(matcher.group());
            }

            String[] holdItems = holdStr.split("[\\u4E00-\\u9FA5]+");
            for (int i = 1; i < holdItems.length; i++) {
                String holdItem = removeStart(holdItems[i]);
                String[] item = holdItem.split(TAB);
                AccountHold accountHold = new AccountHold();
                accountHold.setCode(symbols.get(i - 1));
                accountHold.setQuantity(getInt(item[1]));
                accountHold.setCost(getDouble(item[6]));
                accountHold.setPrice(getDouble(item[7]));
                accountHold.setAccountName(accountAsset.getAccountName());
                accountHold.setDate(accountAsset.getDate());
                accountHolds.add(accountHold);
            }
        }
        result.setAccountHoldList(accountHolds);
        result.setAccountAsset(accountAsset);
        return result;
    }

    private String removeStart(String str) {
        char[] ss = str.toCharArray();
        for (int i = 0; i < ss.length; i++) {
            char s = ss[i];
            if (s >= '0' && s <= '9') {
                return str.substring(i);
            }
        }
        return "";
    }
}

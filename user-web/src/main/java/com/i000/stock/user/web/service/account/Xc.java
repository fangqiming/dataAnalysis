package com.i000.stock.user.web.service.account;

import com.i000.stock.user.api.entity.bo.AccountBO;
import com.i000.stock.user.dao.model.AccountAsset;
import com.i000.stock.user.dao.model.AccountHold;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class Xc implements IPicture {

    @Resource
    private OcrClient ocrClient;

    @Override
    public AccountBO parse(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        AccountAsset accountAsset = getAsset(url);
        accountAsset.setCountry("CN");
        String context = getContext(url, ocrClient);
        Double total = getDouble(context.split("总资产")[1].split("盈亏")[0]);
        accountAsset.setTotal(total);
        String holds = context.split("股东代码\t")[1];
        List<AccountHold> accountHolds = new ArrayList<>();
        String[] holdStr = holds.split("\t[0368]\\d{5}\t");
        for (String holdString : holdStr) {
            AccountHold accountHold = new AccountHold();
            String hold = deleteChar(holdString);
            String[] split = hold.split(TAB);
            accountHold.setCode(split[0]);
            accountHold.setQuantity(getInt(split[1]));
            accountHold.setDate(accountAsset.getDate());
            accountHold.setAccountName(accountAsset.getAccountName());
            accountHold.setPrice(getDouble(split[4]));
            accountHold.setCost(getDouble(split[3]));
            accountHolds.add(accountHold);
        }
        result.setAccountHoldList(accountHolds);
        result.setAccountAsset(accountAsset);
        return result;
    }

    /**
     * 保证获取的字符串是以汉字开头的
     *
     * @return
     */
    private String deleteChar(String str) {
        String[] ss = str.split("");
        for (int i = 0; i < ss.length; i++) {
            //如果匹配发现是汉字
            if (String.valueOf(ss[i]).matches("[\u4e00-\u9fa5]")) {
                return str.substring(i);
            }
        }
        return "";
    }
}

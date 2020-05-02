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
public class Cats implements IPicture {

    @Resource
    private OcrClient ocrClient;


    @Override
    public AccountBO parse(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        AccountAsset accountAsset = getAsset(url);
        String context = getContext(url, ocrClient);
        System.out.println(context);
        Double total = Double.valueOf(context.split("\\d{10,12}\t")[1].split(TAB)[1].replaceAll(",", "").replaceAll(" ", ""));
        List<AccountHold> accountHolds = new ArrayList<>();
        accountAsset.setTotal(total);
        if (context.contains(")")) {
            String[] holdItem = context.split("\\)\t")[1].split(TAB);
            List<List<String>> list = getList(holdItem, 11);
            for (List<String> hold : list) {
                AccountHold accountHold = new AccountHold();
                accountHold.setCode(hold.get(0));
                accountHold.setCost(Double.valueOf(hold.get(5).replaceAll(",", "").replaceAll(" ", "")));
                accountHold.setPrice(Double.valueOf(hold.get(6).replaceAll(",", "").replaceAll(" ", "")));
                accountHold.setQuantity(Integer.valueOf(hold.get(3).replaceAll(",", "").replaceAll(" ", "")));
                accountHold.setAccountName(accountAsset.getAccountName());
                accountHold.setDate(accountAsset.getDate());
                accountHolds.add(accountHold);
            }
        }
        result.setAccountHoldList(accountHolds);
        result.setAccountAsset(accountAsset);
        return result;
    }
}

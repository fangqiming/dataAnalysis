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
public class Ib implements IPicture {

    @Resource
    private OcrClient ocrClient;

    @Override
    public AccountBO parse(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        String context = getContext(url, ocrClient);
        AccountAsset asset = getAsset(url);
        context = context.replaceAll(",", "");
        Double total = Double.valueOf(context.split("USD\\)\t")[1].split(TAB)[0]);
        asset.setTotal(total);
        //创建持仓列表
        List<AccountHold> accountHolds = new ArrayList<>();
        String holdStr = context.split("平均价格\t")[1];
        String[] holdArray = holdStr.split(TAB);
        List<List<String>> list = getList(holdArray, 5);
        for (List<String> hold : list) {
            AccountHold accountHold = new AccountHold();
            accountHold.setCode(hold.get(0));
            accountHold.setCost(Double.valueOf(hold.get(4).replaceAll(",", "").replaceAll(" ", "")));
            accountHold.setPrice(Double.valueOf(hold.get(3).replaceAll(",", "").replaceAll(" ", "")));
            accountHold.setQuantity(Integer.valueOf(hold.get(1).replaceAll(",", "").replaceAll(" ", "")));
            accountHold.setAccountName(asset.getAccountName());
            accountHold.setDate(asset.getDate());
            accountHolds.add(accountHold);
        }
        result.setAccountAsset(asset);
        result.setAccountHoldList(accountHolds);
        return result;
    }
}

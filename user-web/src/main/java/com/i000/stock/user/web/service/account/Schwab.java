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
public class Schwab implements IPicture {

    @Resource
    private OcrClient ocrClient;

    @Override
    public AccountBO parse(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        String context = getContext(url, ocrClient);
        AccountAsset asset = getAsset(url);
        asset.setCountry("US");
        context = context.replaceAll("\\$", "")
                .replaceAll(",", "").replaceAll("\\+", "");
        String[] split = context.split("Account Value\t");
        Double total = getDouble(split[1].split(TAB)[0]);
        String[] split1 = context.split("Mkt ValueG.L\t");
        List<AccountHold> accountHolds = new ArrayList<>();
        asset.setTotal(total);
        if (split1.length > 1) {
            String holdStr = split1[1];

            if (holdStr.contains("ETFs\t")) {
                String[] holdSymbol = holdStr.split("ETFs\t");
                String security = holdSymbol[0];
                String etf = holdSymbol[1].split("Cash Investments")[0];
                String[] securityStr = security.split("\\)\t");
                String[] etfs = etf.split("\\)\t");
                for (String sec : securityStr) {
                    addSchPosition(asset, sec, accountHolds);
                }
                for (String sec : etfs) {
                    addSchPosition(asset, sec, accountHolds);
                }
            } else {
                String holdSymbol = holdStr.split("Cash Investments")[0];
                String[] securityStr = holdSymbol.split("\\)\t");
                for (String sec : securityStr) {
                    addSchPosition(asset, sec, accountHolds);
                }
            }
        } else {
            //普通账户
            String holdStr = context.split("Chang")[2];
            String holdSymbol = holdStr.split("Cash Investments")[0];
            holdSymbol = removeStart(holdSymbol);
            String[] securityStr = holdSymbol.split("\\)\t");
            for (String sec : securityStr) {
                if (sec.length() > 10) {
                    addSchPosition(asset, sec, accountHolds);
                }
            }
        }

        result.setAccountAsset(asset);
        result.setAccountHoldList(accountHolds);
        return result;
    }

    private void addSchPosition(AccountAsset asset, String sec, List<AccountHold> accountHolds) {
        sec = sec.replaceAll("ETFs\t", "");
        AccountHold hold = new AccountHold();
        String[] holdItem = sec.split(TAB);
        hold.setAccountName(asset.getAccountName());
        hold.setDate(asset.getDate());
        hold.setPrice(getDouble(holdItem[1]));
        hold.setCode(holdItem[0]);
        hold.setQuantity(getInt(holdItem[holdItem.length - 2]));
        accountHolds.add(hold);
    }

    /**
     * 去掉行首为非大写字母的字符
     *
     * @param str
     * @return
     */
    private String removeStart(String str) {
        String[] ss = str.split("");
        for (int i = 0; i < ss.length; i++) {
            String s = ss[i];
            if (PATTERN.matcher(s).find()) {
                return str.substring(i);
            }
        }
        return "";
    }


}

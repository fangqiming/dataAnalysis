package com.i000.stock.user.web.service;

import com.i000.stock.user.api.entity.bo.AccountBO;
import com.i000.stock.user.dao.model.AccountAsset;
import com.i000.stock.user.dao.model.AccountHold;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class TencentService {

    @Resource
    private OcrClient ocrClient;

    private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static String TAB = "\t";

    private static Pattern PATTERN = Pattern.compile("[A-Z]");

    public void print(String url) {

    }

    /**
     * 获取CATS的图片解析结果 包含刘桥基金,君合AI一号,检查一下看是否有优化的地方
     *
     * @param url
     * @return
     * @throws TencentCloudSDKException
     */
    public AccountBO getCATSResult(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        AccountAsset accountAsset = getAsset(url);
        String context = getContext(url);
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

    /**
     * 获取IB的图片解析结果,包含老虎证券,雪盈证券
     *
     * @param url
     * @return
     * @throws TencentCloudSDKException
     */
    public AccountBO getIBResult(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        String context = getContext(url);
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

    /**
     * 解析嘉信证券的截图 包含,嘉信证券,嘉信IRA
     *
     * @param url
     * @return
     * @throws TencentCloudSDKException
     */
    public AccountBO getSCHResult(String url) throws TencentCloudSDKException {
        AccountBO result = new AccountBO();
        String context = getContext(url);
        AccountAsset asset = getAsset(url);
        context = context.replaceAll("\\$", "")
                .replaceAll(",", "").replaceAll("\\+", "");
        String[] split = context.split("Account Value\t");
        Double total = Double.valueOf(split[1].split(TAB)[0]);
        String[] split1 = context.split("Mkt ValueG.*L\t");
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
                addSchPosition(asset, sec, accountHolds);
            }
        }

        result.setAccountAsset(asset);
        result.setAccountHoldList(accountHolds);
        return result;

    }


    /**
     *
     * @param asset
     * @param sec
     * @param accountHolds
     */

    private void addSchPosition(AccountAsset asset, String sec, List<AccountHold> accountHolds) {
        AccountHold hold = new AccountHold();
        String[] holdItem = sec.split(TAB);
        hold.setAccountName(asset.getAccountName());
        hold.setDate(asset.getDate());
        hold.setPrice(Double.valueOf(holdItem[1]));
        hold.setCode(holdItem[0]);
        hold.setQuantity(Integer.valueOf(holdItem[holdItem.length - 2].replaceAll("\\.", "")));
        accountHolds.add(hold);
    }


    private AccountAsset getAsset(String url) {
        AccountAsset result = new AccountAsset();
        String[] accountInfo = url.split("_");
        String accountName = accountInfo[1];
        String type = accountInfo[2];
        LocalDate date = LocalDate.parse(accountInfo[3], DF);
        Double share = Double.valueOf(accountInfo[4].split("\\.")[0]);
        result.setAccountName(accountName);
        result.setType(type);
        result.setDate(date);
        result.setShare(share);
        return result;
    }

    public String getContext(String url) throws TencentCloudSDKException {
        StringBuffer result = new StringBuffer();
        GeneralAccurateOCRRequest request = new GeneralAccurateOCRRequest();
        request.setImageUrl(url);
        GeneralAccurateOCRResponse response = ocrClient.GeneralAccurateOCR(request);
        TextDetection[] textDetections = response.getTextDetections();
        for (TextDetection text : textDetections) {
            result.append(text.getDetectedText()).append(TAB);
        }
        return result.toString();
    }


    private List<List<String>> getList(String[] holdItem, int qty) {
        List<List<String>> result = new ArrayList<>();
        List<String> mList = Arrays.asList(holdItem);

        if (mList.size() % qty != 0) {
            for (int j = 0; j < mList.size() / qty + 1; j++) {
                if ((j * qty + qty) < mList.size()) {
                    result.add(mList.subList(j * qty, j * qty + qty));//0-3,4-7,8-11    j=0,j+3=3   j=j*3+1
                } else if ((j * qty + qty) > mList.size()) {
                    result.add(mList.subList(j * qty, mList.size()));
                } else if (mList.size() < qty) {
                    result.add(mList.subList(0, mList.size()));
                }
            }
        } else if (mList.size() % qty == 0) {
            for (int j = 0; j < mList.size() / qty; j++) {
                if ((j * qty + qty) <= mList.size()) {
                    result.add(mList.subList(j * qty, j * qty + qty));//0-3,4-7,8-11    j=0,j+3=3   j=j*3+1
                } else if ((j * qty + qty) > mList.size()) {
                    result.add(mList.subList(j * qty, mList.size()));
                } else if (mList.size() < qty) {
                    result.add(mList.subList(0, mList.size()));
                }
            }
        }
        return result;
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

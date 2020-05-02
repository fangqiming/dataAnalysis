package com.i000.stock.user.web.service.account;

import com.i000.stock.user.api.entity.bo.AccountBO;
import com.i000.stock.user.dao.model.AccountAsset;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.GeneralAccurateOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.GeneralAccurateOCRResponse;
import com.tencentcloudapi.ocr.v20181119.models.TextDetection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public interface IPicture {

    DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    String TAB = "\t";

    Pattern PATTERN = Pattern.compile("[A-Z]");

    /**
     * 根据URL解析图片内容
     *
     * @param url
     * @return
     */
    AccountBO parse(String url) throws TencentCloudSDKException;

    /**
     * 获取图片中的文字信息
     *
     * @param url
     * @param ocrClient
     * @return
     * @throws TencentCloudSDKException
     */
    default String getContext(String url, OcrClient ocrClient) throws TencentCloudSDKException {
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

    /**
     * 解析URL获取URL中携带的账户信息
     *
     * @param url
     * @return
     */
    default AccountAsset getAsset(String url) {
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

    /**
     * 对List中的元素进行分组,比如对16个元素按照每4个元素进行分组
     *
     * @param holdItem
     * @param qty
     * @return
     */
    default List<List<String>> getList(String[] holdItem, int qty) {
        List<List<String>> result = new ArrayList<>();
        List<String> mList = Arrays.asList(holdItem);

        if (mList.size() % qty != 0) {
            for (int j = 0; j < mList.size() / qty + 1; j++) {
                if ((j * qty + qty) < mList.size()) {
                    result.add(mList.subList(j * qty, j * qty + qty));
                } else if ((j * qty + qty) > mList.size()) {
                    result.add(mList.subList(j * qty, mList.size()));
                } else if (mList.size() < qty) {
                    result.add(mList.subList(0, mList.size()));
                }
            }
        } else if (mList.size() % qty == 0) {
            for (int j = 0; j < mList.size() / qty; j++) {
                if ((j * qty + qty) <= mList.size()) {
                    result.add(mList.subList(j * qty, j * qty + qty));
                } else if ((j * qty + qty) > mList.size()) {
                    result.add(mList.subList(j * qty, mList.size()));
                } else if (mList.size() < qty) {
                    result.add(mList.subList(0, mList.size()));
                }
            }
        }
        return result;
    }
}

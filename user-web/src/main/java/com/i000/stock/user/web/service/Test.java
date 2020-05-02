package com.i000.stock.user.web.service;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.cvm.v20170312.CvmClient;
import com.tencentcloudapi.cvm.v20170312.models.DescribeZonesRequest;
import com.tencentcloudapi.cvm.v20170312.models.DescribeZonesResponse;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.GeneralBasicOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.GeneralBasicOCRResponse;
import com.tencentcloudapi.ocr.v20181119.models.TextDetection;

public class Test {
    public static void main(String[] args) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey
            Credential cred = new Credential("AKIDmMhZAaOSOZeS1vgjYEI5MMCQMlulbdeP", "cxZuyYtiGXXGjvqVS9suLQFAGAf28zxf");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod(ClientProfile.SIGN_TC3_256);
            OcrClient ocrClient = new OcrClient(cred, "ap-guangzhou");
            GeneralBasicOCRRequest req = new GeneralBasicOCRRequest();
            req.setImageUrl("https://aisharev1.oss-cn-beijing.aliyuncs.com/share/lqji_2_2020-04-29_10000000.png");
            GeneralBasicOCRResponse response = ocrClient.GeneralBasicOCR(req);
            TextDetection[] textDetections = response.getTextDetections();
            for (TextDetection t : textDetections) {
                System.out.println(t.getDetectedText());
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }

    }
}

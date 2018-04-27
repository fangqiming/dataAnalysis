package com.i000.stock.user.core.file.oss;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

import java.net.URL;
import java.util.Date;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:41 2018/1/16
 * @Modified By:
 */
public class OSSUtil {

    private static final String DEFAULT_STYLE = "image/resize,w_750,h_480";
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String catalog;

    public String createSign(String fileName, Date expireTime) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        return ossClient.generatePresignedUrl(bucketName, catalog + "/" + fileName, expireTime).toString();
    }

    public String createSignWithStyle(String fileName, Date expireTime) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, catalog + "/" + fileName, HttpMethod.GET);
        req.setExpiration(expireTime);
        req.setProcess(DEFAULT_STYLE);
        URL signedUrl = ossClient.generatePresignedUrl(req);
        return signedUrl.toString();
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

}
package com.i000.stock.user.core.file.oss;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.i000.stock.user.core.file.upload.FileStreamTransformer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:41 2018/1/16
 * @Modified By:
 */
public class OSSFileUpload {

    private String httpPrefix;

    private String writeEndpoint;

    private String readEndpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

    private String catalog;

    /**
     * 文件上传
     *
     * @return 上传成功后的文件地址
     */
    public String upload(FileStreamTransformer fileStreamTransformer, boolean createNewName) {
        ObjectMetadata meta = new ObjectMetadata();
        String fileName = generateFileName(fileStreamTransformer.getFileName(), createNewName);

        meta.setContentLength(fileStreamTransformer.getSize());

        new OSSClient(writeEndpoint, accessKeyId, accessKeySecret).putObject(
                bucketName,
                fileName,
                fileStreamTransformer.getInputStream(),
                meta
        );

        return buildFileURL(fileName);
    }

    private String generateFileName(String oldFileName, boolean createNewName) {
        return createNewName ? catalog + "/" + UUID.randomUUID().toString().replace("-", StringUtils.EMPTY)
                + "." + FilenameUtils.getExtension(oldFileName)
                : catalog + "/" + oldFileName;

    }

    private String buildFileURL(String fileName) {
        StringBuilder sb = new StringBuilder();
        sb.append(httpPrefix)
                .append(bucketName).append(".")
                .append(readEndpoint.substring(httpPrefix.length()))
                .append("/").append(fileName);

        return sb.toString();
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

    public void setHttpPrefix(String httpPrefix) {
        this.httpPrefix = httpPrefix;
    }

    public void setWriteEndpoint(String writeEndpoint) {
        this.writeEndpoint = writeEndpoint;
    }

    public void setReadEndpoint(String readEndpoint) {
        this.readEndpoint = readEndpoint;
    }
}
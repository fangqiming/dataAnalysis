package com.i000.stock.user.core.file.upload;

import java.io.InputStream;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:41 2018/1/16
 * @Modified By:
 */
public interface FileStreamTransformer {

    /**
     * 参数名称
     */
    String getParameterName();

    /**
     * 文件名称
     */
    String getFileName();

    /**
     * 文件流
     */
    InputStream getInputStream();

    /**
     * contentType
     */
    String getContentType();

    /**
     * 文件大小
     */
    long getSize();

    /**
     * 文件数据
     */
    byte[] getData();

}
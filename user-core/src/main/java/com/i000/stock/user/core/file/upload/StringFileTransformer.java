package com.i000.stock.user.core.file.upload;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 12:41 2018/1/16
 * @Modified By:
 */
public class StringFileTransformer implements FileStreamTransformer {

    private byte[] data;
    private String fileName;

    public StringFileTransformer(String fileName, String content) {
        this.fileName = fileName;
        this.data = content.getBytes();
    }

    @Override
    public String getParameterName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(data);
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public byte[] getData() {
        return data;
    }
}
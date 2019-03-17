package com.i000.stock.user.api.service.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @Author:qmfang
 * @Description: 用于数据的备份和恢复
 * @Date:Created in 14:06 2018/6/14
 * @Modified By:
 */
public interface FileService {

    /**
     * 将收到的推进信息保存到到文件
     *
     * @param content
     * @throws IOException
     */
    void saveFile(String content, String path);

    /**
     * 从推荐文件中中恢复数据
     *
     * @param start
     * @param end
     * @return
     */
    String restoreData(String start, String end);


    /**
     * 将文件保存并返回文件对象
     *
     * @param file
     * @param path
     * @return
     */
    File parseGz(MultipartFile file, String path) throws IOException;
}

package com.i000.stock.user.web.controller;

import com.i000.stock.user.core.file.oss.OSSFileUpload;
import com.i000.stock.user.core.file.upload.FileStreamTransformer;
import com.i000.stock.user.core.file.upload.SpringMultipartFileTransformer;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 20:06 2018/4/27
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private OSSFileUpload ossFileUpload;

    /**
     * 用于上传图片
     * 127.0.0.1:8082/file
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/upload")
    public ResultEntity testFile(@RequestParam("file") MultipartFile file) throws IOException {
        FileStreamTransformer fileStreamTransformer = SpringMultipartFileTransformer.transformer(file);
        String url = ossFileUpload.upload(fileStreamTransformer);
        return Results.newNormalResultEntity("url", url);
    }
}

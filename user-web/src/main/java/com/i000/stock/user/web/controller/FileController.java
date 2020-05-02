package com.i000.stock.user.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.i000.stock.user.api.entity.bo.AccountBO;
import com.i000.stock.user.api.service.util.FileService;
import com.i000.stock.user.core.file.oss.OSSFileUpload;
import com.i000.stock.user.core.file.upload.FileStreamTransformer;
import com.i000.stock.user.core.file.upload.SpringMultipartFileTransformer;
import com.i000.stock.user.core.result.Results;
import com.i000.stock.user.core.result.base.ResultEntity;
import com.i000.stock.user.service.impl.AccountService;
import com.i000.stock.user.web.service.TencentService;
import com.i000.stock.user.web.service.account.IPicture;
import com.i000.stock.user.web.service.account.PictureParseHelp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 20:06 2018/4/27
 * @Modified By:
 */
@Slf4j
@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {

    @Autowired
    private OSSFileUpload ossFileUpload;

    @Autowired
    private FileService fileService;

    @Autowired
    private PictureParseHelp pictureParseHelp;

    @Resource
    private AccountService accountService;

    @GetMapping(value = "/restore_data")
    public ResultEntity restoreData(@RequestParam String start, @RequestParam String end) {
        String result = fileService.restoreData(start, end);
        return Results.newNormalResultEntity("result", result);
    }

    /**
     * 用于上传图片
     * 127.0.0.1:8082/file/upload
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/upload")
    public ResultEntity fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        FileStreamTransformer fileStreamTransformer = SpringMultipartFileTransformer.transformer(file);
        System.out.println(fileStreamTransformer.getFileName());
        String url = ossFileUpload.upload(fileStreamTransformer, false);
        return Results.newNormalResultEntity("url", url);
    }

    @PostMapping(path = "/upload_user")
    public JSONObject fileUploadBy(@RequestParam("file") MultipartFile file) throws IOException {
        FileStreamTransformer fileStreamTransformer = SpringMultipartFileTransformer.transformer(file);
        String url = ossFileUpload.upload(fileStreamTransformer, true);
        JSONObject result = new JSONObject();
        result.put("errno", 0);
        result.put("data", Arrays.asList(url));
        return result;
    }

    /**
     * 用于文件解析的
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping(path = "/account_upload")
    public JSONObject account_upload(@RequestParam("file") MultipartFile file) throws Exception {
        FileStreamTransformer fileStreamTransformer = SpringMultipartFileTransformer.transformer(file);
        String url = ossFileUpload.upload(fileStreamTransformer, false);
        IPicture iPicture = pictureParseHelp.get(url);
        AccountBO accountBO = iPicture.parse(url);
        accountService.save(accountBO);
        JSONObject result = new JSONObject();
        result.put("errno", 0);
        result.put("data", url);
        return result;
    }

}

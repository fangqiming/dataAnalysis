package com.i000.stock.user.service.impl;

import com.i000.stock.user.api.service.util.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 14:06 2018/6/14
 * @Modified By:
 */
@Slf4j
@Component
public class FileServiceImpl implements FileService {

    @Autowired
    private RestTemplate restTemplate;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    private static final String url = "http://127.0.0.1:8081/engine/receive_recommend?needSave=0";

    @Override
    public void saveFile(String content, String path) {
        try {
            File folder = new File("./" + path);
            if (!folder.exists()) {
                System.out.println("文件已创建");
                folder.mkdir();
            }
            File file = new File(String.format("./%s/%s.report", path, sdf.format(new Date())));
            file.deleteOnExit();
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes("UTF-8"));
            fos.close();
        } catch (IOException e) {
            log.error("FILE WRITE ERROR e=[{}]", e);
        }
    }


    @Override
    public String restoreData(String start, String end) {
        if (StringUtils.isBlank(start) || StringUtils.isBlank(end)) {
            log.error("[RESTORE DATA ERROR] start=[{}] end=[{}]", start, end);
            return "param error";
        }
        start += start + ".report";
        end += end + ".report";
        File file = new File("./recommend");
        File[] files = file.listFiles();
        List<File> fileList = new ArrayList<>(Arrays.asList(files));
        fileList.stream().sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
        for (File report : fileList) {
            if (report.getName().compareTo(start) >= 0 || report.getName().compareTo(end) <= 0) {
                String content = readToString(report);
                HttpEntity<String> formEntity = new HttpEntity<String>(content, null);
                String result = restTemplate.postForObject(url, formEntity, String.class);
                if (!result.contains("success")) {
                    return "error " + report.getName();
                }
                try {
                    Thread.sleep(4000);
                } catch (Exception e) {
                }
            }
        }
        return end;
    }

    private String readToString(File file) {
        String encoding = "UTF-8";
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(fileContent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将Gz文件解压并且读取文件内容
     */
    @Override
    public File parseGz(MultipartFile file, String path) throws IOException {
        File outdir = new File(path);
        GZIPInputStream gzin = new GZIPInputStream(file.getInputStream());
        String name = String.format("%s_Rank", LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd")));
        this.extractFile(gzin, outdir, name);
        return new File(outdir + name);
    }


    private void extractFile(GZIPInputStream in, File outdir, String name) throws IOException {
        byte[] buffer = new byte[1024];
        BufferedOutputStream out = new BufferedOutputStream(
                new FileOutputStream(new File(outdir + name)));
        int count = -1;
        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }
        out.close();
    }
}

package com.i000.stock.user.api.service;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:31 2018/4/27
 * @Modified By:
 */
public interface MailParseService {

    Pattern section = Pattern.compile("\n\n");
    Pattern line = Pattern.compile("\n");


    Pattern date = Pattern.compile("^[0-9]{4}\\/[0-9]{2}\\/[0-9]{2}");

    Pattern validItem = Pattern.compile(".*\t.*\t.*\t.*\t.*\t.*\t.*\t.*\t.*");


    /**
     * 将字符串解析成对象保存到数据库中
     *
     * @param original
     * @return
     */
    LocalDate save(String original);

}

package com.i000.stock.user.api.service.original;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:31 2018/4/27
 * @Modified By:
 */
public interface ParseService {

    Pattern SECTION = Pattern.compile("\n\n");
    Pattern LINE = Pattern.compile("\n");


    Pattern DATE = Pattern.compile("^[0-9]{4}\\/[0-9]{2}\\/[0-9]{2}");

    Pattern VALID_ITEM = Pattern.compile(".*\t.*\t.*\t.*\t.*\t.*\t.*\t.*\t.*");


    /**
     * 将字符串解析成对象保存到数据库中
     *
     * @param original
     * @return
     */
    LocalDate save(String original, LocalDate date);

}

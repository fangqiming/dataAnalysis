package com.i000.stock.user.web.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 10:00 2018/5/3
 * @Modified By:
 */
public class String2LocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(String source) {
        return LocalDate.parse(source, DateTimeFormatter.ofPattern("yy-MM-dd"));
    }
}

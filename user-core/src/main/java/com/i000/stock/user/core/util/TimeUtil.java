package com.i000.stock.user.core.util;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:05 2018/1/20
 * @Modified By:
 */
public class TimeUtil {

    public static LocalDate max(LocalDate l1, LocalDate l2) {
        return l1.compareTo(l2) > 0 ? l1 : l2;
    }

    public static String localData2String(LocalDate localDate, String format) {
        assertString(format, "没有指定日期的格式");
        if (!Objects.isNull(localDate)) {
            return localDate.format(DateTimeFormatter.ofPattern(format));
        }
        return null;
    }

    public static List<String> localData2StringList(List<LocalDate> localDates, String format) {
        if (CollectionUtils.isEmpty(localDates)) {
            return new ArrayList<>(0);
        }
        return localDates.stream().map(a -> localData2String(a, format)).collect(toList());
    }

    public static String localData2String(LocalDate localDate) {
        return localData2String(localDate, "yyyy-MM-dd");
    }

    public static List<String> localData2StringList(List<LocalDate> localDates) {
        return localData2StringList(localDates, "yyyy-MM-dd");
    }


    public static void assertString(String str, String message) {
        if (StringUtils.isBlank(str)) {
            throw new NullPointerException(message);
        }
    }
}

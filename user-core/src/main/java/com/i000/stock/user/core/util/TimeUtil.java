package com.i000.stock.user.core.util;


import com.i000.stock.user.core.constant.enums.ApplicationErrorMessage;
import com.i000.stock.user.core.constant.enums.TimeZoneEnum;
import com.i000.stock.user.core.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 13:05 2018/1/20
 * @Modified By:
 */
public class TimeUtil {

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public static void main(String[] args) {
        LocalDateTime dateTimeByTimeZone = getDateTimeByTimeZone(TimeZoneEnum.NEW_YORK);
        System.out.println(dateTimeByTimeZone.format(DATE_FORMATTER));

    }

    public static LocalDateTime getDateTimeByTimeZone(TimeZoneEnum timeZoneEnum) {
        float timeZoneOffset = timeZoneEnum.getValue();
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }

        int newTime = (int) (timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        return LocalDateTime.parse(sdf.format(new Date()), DATE_FORMATTER);
    }

    /**
     * @param str 字符串格式为 2019-01-26 05:50:25
     * @return
     */
    public static LocalDateTime getNYTimeByBJTime(String str) {

        float timeZoneOffset = TimeZoneEnum.NEW_YORK.getValue();
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }

        int newTime = (int) (timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        try {
            Date bj = sdf2.parse(str);
            return LocalDateTime.parse(sdf.format(bj), DATE_FORMATTER);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        throw new ServiceException(ApplicationErrorMessage.TIME_FORMATE_ERROR);
    }
}

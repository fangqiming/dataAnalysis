package com.i000.stock.user.service.impl.us;

import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class PatternUtil {
    public static final Pattern LONG1_HOLD = Pattern.compile("------ Portfolio for [0-9]{8} , LONG1 ------");
    public static final String LONG1_HOLD_KEY = "------ Portfolio for HH , LONG1 ------";
    public static final Pattern LONG2_HOLD = Pattern.compile("------ Portfolio for [0-9]{8} , LONG2 ------");
    public static final String LONG2_HOLD_KEY = "------ Portfolio for HH , LONG2 ------";
    public static final Pattern SHORT_HOLD = Pattern.compile("------ Portfolio for [0-9]{8} , SHORT ------");
    public static final String SHORT_HOLD_KEY = "------ Portfolio for HH , SHORT ------";

    public static final Pattern LONG1_TRADE = Pattern.compile("------ Details for closed positions on [0-9]{8} , LONG1 ------");
    public static final String LONG1_TRADE_KEY = "------ Details for closed positions on HH , LONG1 ------";
    public static final Pattern LONG2_TRADE = Pattern.compile("------ Details for closed positions on [0-9]{8} , LONG2 ------");
    public static final String LONG2_TRADE_KEY = "------ Details for closed positions on HH , LONG2 ------";
    public static final Pattern SHORT_TRADE = Pattern.compile("------ Details for closed positions on [0-9]{8} , SHORT ------");
    public static final String SHORT_TRADE_KEY = "------ Details for closed positions on HH , SHORT ------";

    public static final Pattern TODAY_TRADE = Pattern.compile("------ Todays Trades ------");
    public static final String TODAY_TRADE_KEY = "------ Todays Trades ------";
    public static final Pattern TOMORROW_PLAN = Pattern.compile("------ Tomorrows Plan ------");
    public static final String TOMORROW_PLAN_KEY = "------ Tomorrows Plan ------";

    public static final Pattern SECTION = Pattern.compile("\n\n");
    public static final Pattern TAB = Pattern.compile("\t");
    public static final Pattern TWO_BLANK = Pattern.compile(" {2,}");
    public static final Pattern LINE = Pattern.compile("\n");
    public static final Pattern DATE = Pattern.compile("^[0-9]{4}\\/[0-9]{2}\\/[0-9]{2}");
    public static final Pattern VALID_ITEM = Pattern.compile(".*\t.*\t.*\t.*\t.*\t.*\t.*\t.*\t.*");
    public static final Pattern VALID_ITEM_BLANK = Pattern.compile(".* {2,}.* {2,}.* {2,}.* {2,}.* {2,}.* {2,}.* {2,}.* {2,}.*");

    public static final DateTimeFormatter DF_SLANT = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyyMMdd");

}

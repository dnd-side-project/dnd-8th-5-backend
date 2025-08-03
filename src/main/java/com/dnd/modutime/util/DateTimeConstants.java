package com.dnd.modutime.util;

import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * 시간관련 상수
 */
public class DateTimeConstants {
    public static final String FORMAT_DEFAULT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_DATE_TIME_WITHOUT_T = "yyyy-MM-dd HH:mm:ss";
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");
    public static final ZoneOffset DEFAULT_ZONE_OFF = ZoneOffset.of("+09:00");

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final int FIRST_DAY_OF_MONTH = 1;

    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_TIME_WITHOUT_S = "HH:mm";
}

package com.dnd.modutime.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtils {

    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Seoul");

    private DateTimeUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ZoneId serverZoneId() {
        return ZoneId.of("UTC");
    }

    public static LocalDateTime currentUTC() {
        return LocalDateTime.now(serverZoneId());
    }

    public static LocalDateTime convertDateToLocalDateTime(Date date) {
        return date.toInstant().atZone(serverZoneId()).toLocalDateTime();
    }

    public static String toISO8601(Date date) {
        return date.toInstant()
                .atZone(serverZoneId())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

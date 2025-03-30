package com.dnd.modutime.core.timeblock.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DateTimeToAvailableDateTimeConvertorFactoryTest {

    private DateTimeToAvailableDateTimeConvertorFactory dateTimeToAvailableDateTimeConvertorFactory;

    @BeforeEach
    void setUp() {
        var dateTimeConvertor = Map.of("dateTimeConvertor", new DateTimeConvertor(), "dateConvertor", new DateConvertor());
        dateTimeToAvailableDateTimeConvertorFactory = new DateTimeToAvailableDateTimeConvertorFactory(dateTimeConvertor);
    }

    @Test
    void hasTime이_true이면_dateTimeConvertor를_반환한다() {
        DateTimeToAvailableDateTimeConvertor instance = dateTimeToAvailableDateTimeConvertorFactory.getInstance(true);
        assertThat(instance).isInstanceOf(DateTimeConvertor.class);
    }

    @Test
    void hasTime이_false이면_dateConvertor를_반환한다() {
        DateTimeToAvailableDateTimeConvertor instance = dateTimeToAvailableDateTimeConvertorFactory.getInstance(false);
        assertThat(instance).isInstanceOf(DateConvertor.class);
    }
}

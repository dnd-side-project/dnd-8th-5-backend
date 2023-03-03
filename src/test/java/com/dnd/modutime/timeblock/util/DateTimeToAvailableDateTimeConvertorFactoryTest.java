package com.dnd.modutime.timeblock.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DateTimeToAvailableDateTimeConvertorFactoryTest {

    @Autowired
    private DateTimeToAvailableDateTimeConvertorFactory dateTimeToAvailableDateTimeConvertorFactory;

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

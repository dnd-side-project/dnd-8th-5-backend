package com.dnd.modutime.core.room.domain;

import static com.dnd.modutime.fixture.RoomFixture.*;
import static com.dnd.modutime.fixture.TimeFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.dnd.modutime.util.FakeTimeProvider;

public class RoomTest {

    @Test
    void 방이_생성되면_Uuid를_생성한다() {
        Room room = getRoom();
        assertThat(room.getUuid()).isNotNull();
    }

    @Test
    void 날짜가_비어있으면_예외가_발생한다() {
        List<LocalDate> dates = List.of();
        assertThatThrownBy(() -> getRoom(dates))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 날짜가_null_이면_예외가_발생한다() {
        assertThatThrownBy(() -> getRoomByRoomDates(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 시작시간만_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getRoomByStartEndTime(null, _13_00))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 끝시간만_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getRoomByStartEndTime(_12_00, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 시작시간과_끝나는시간이_둘다_null일때_시간을_꺼내면_null을_반환한다() {
        Room room = getRoomByStartEndTime(null, null);
        assertAll(
                () -> assertThat(room.getStartTimeOrNull()).isNull(),
                () -> assertThat(room.getEndTimeOrNull()).isNull()
        );
    }

    @ParameterizedTest
    @MethodSource("provideAllTime")
    void 시작시간과_끝시간이_같으면_예외가_발생한다(LocalTime time) {
        assertThatThrownBy(() -> getRoomByStartEndTime(time, time))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 방참여인원이_음수이면_예외가_발생한다() {
        assertThatThrownBy(() -> getRoomByHeadCount(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 방참여인원이_null_일때_참여인원을_꺼내면_null을_반환한다() {
        Room room = getRoomByHeadCount(null);
        assertThat(room.getHeadCountOrNull()).isNull();
    }

    @Test
    void 방의_마감시간이_null이면_마감시간을_꺼내면_null을_반환한다() {
        Room room = getRoom(null, new FakeTimeProvider());
        assertThat(room.getDeadLineOrNull()).isNull();
    }

    @Test
    void 마감시간이_현재시간보다_작으면_예외가_발생한다() {
        FakeTimeProvider timeProvider = new FakeTimeProvider();
        timeProvider.setTime(_2023_02_20_00_00);
        assertThatThrownBy(() -> getRoom(_2023_02_10_00_00, timeProvider))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 마감시간이_현재시간과_같으면_예외가_발생한다() {
        FakeTimeProvider timeProvider = new FakeTimeProvider();
        timeProvider.setTime(_2023_02_10_00_00);
        assertThatThrownBy(() -> getRoom(_2023_02_10_00_00, timeProvider))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 방제목이_null이면_예외를_반환한다() {
        assertThatThrownBy(() -> getRoomByTitle(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 방제목이_빈문자이면_예외를_반환한다() {
        assertThatThrownBy(() -> getRoomByTitle(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 들어온_날짜들이_방의_날짜들안에_모두_포함되면_true를_반환한다() {
        Room room = getRoomByRoomDates(List.of(new RoomDate(_2023_02_09), new RoomDate(_2023_02_10)));
        assertThat(room.containsAllDates(List.of(new RoomDate(_2023_02_09), new RoomDate(_2023_02_10)))).isTrue();
    }

    @Test
    void 들어온_날짜들중_하나라도_방의_날짜들안에_모두_포함되지않으면_false를_반환한다() {
        Room room = getRoomByRoomDates(List.of(new RoomDate(_2023_02_09), new RoomDate(_2023_02_10)));
        assertThat(room.containsAllDates(List.of(new RoomDate(_2023_02_08), new RoomDate(_2023_02_09)))).isFalse();
    }

    @Test
    void 방의_시작시간과_끝나는시간이_모두_null이면_가지고있지_않다고_판단한다() {
        Room room = getRoomByStartEndTime(null, null);
        assertThat(room.hasStartAndEndTime()).isFalse();
    }

    @Test
    void 방의_시작시간과_끝나는시간이_모두_null이_아니면_가지고있다고_판단한다() {
        Room room = getRoomByStartEndTime(_12_00, _13_00);
        assertThat(room.hasStartAndEndTime()).isTrue();
    }

    @Test
    void 시작시간의_이전_시간이_들어오면_false를_반환한다() {
        Room room = getRoomByStartEndTime(_12_00, _13_00);
        assertThat(room.includeTime(_11_00)).isFalse();
    }

    @Test
    void 끝나는시간과_같은_시간이_들어오면_예외가_발생한다() {
        Room room = getRoomByStartEndTime(_12_00, _13_00);
        assertThat(room.includeTime(_13_00)).isFalse();
    }

    @Test
    void 끝나는시간의_이후_시간이_들어오면_예외가_발생한다() {
        Room room = getRoomByStartEndTime(_12_00, _13_00);
        assertThat(room.includeTime(_14_00)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLocalTimeBetweenStartAndEndTime")
    void 시작시간이_끝시간보다_큰_경우_사이값의_시간이_포함되면_예외가_발생한다(LocalTime invalidTime) {
        Room room = getRoomByStartEndTime(_13_00, _11_00);
        assertThat(room.includeTime(invalidTime)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLocalTimeNotBetweenStartAndEndTime")
    void 시작시간이_끝시간보다_큰_경우_사이값의_시간이_포함되지_않으면_예외가_발생하지_않는다(LocalTime validTime) {
        Room room = getRoomByStartEndTime(_13_00, _11_00);
        assertThat(room.includeTime(validTime)).isTrue();
    }

    @ParameterizedTest
    @MethodSource("provideLocalTimeBetweenStartAndEndTimeWhenEndTimeIsZero")
    void 끝시간이_00시이고_시작시간이_끝시간보다_큰_경우_사이값의_시간이_포함되면_예외가_발생한다(LocalTime invalidTime) {
        Room room = getRoomByStartEndTime(_23_00, _00_00);
        assertThat(room.includeTime(invalidTime)).isFalse();
    }

    @ParameterizedTest
    @MethodSource("provideLocalTimeNotBetweenStartAndEndTimeWhenEndTimeIsZero")
    void 끝시간이_00시이고_시작시간이_끝시간보다_큰_경우_사이값의_시간이_포함되지_않으면_예외가_발생하지_않는다(LocalTime validTime) {
        Room room = getRoomByStartEndTime(_23_00, _00_00);
        assertThat(room.includeTime(validTime)).isTrue();
    }

    private static Stream<Arguments> provideLocalTimeBetweenStartAndEndTime() {
        return Stream.of(
            Arguments.of(_11_00),
            Arguments.of(_11_30),
            Arguments.of(_12_00),
            Arguments.of(_12_30)
        );
    }

    private static Stream<Arguments> provideLocalTimeNotBetweenStartAndEndTime() {
        return Stream.of(
            Arguments.of(_13_00),
            Arguments.of(_13_30),
            Arguments.of(_14_00),
            Arguments.of(_14_30),
            Arguments.of(_15_00),
            Arguments.of(_15_30),
            Arguments.of(_16_00),
            Arguments.of(_16_30),
            Arguments.of(_17_00),
            Arguments.of(_17_30),
            Arguments.of(_18_00),
            Arguments.of(_18_30),
            Arguments.of(_19_00),
            Arguments.of(_19_30),
            Arguments.of(_20_00),
            Arguments.of(_20_30),
            Arguments.of(_21_00),
            Arguments.of(_21_30),
            Arguments.of(_22_00),
            Arguments.of(_22_30),
            Arguments.of(_23_00),
            Arguments.of(_23_30),
            Arguments.of(_00_00),
            Arguments.of(_00_30),
            Arguments.of(_01_00),
            Arguments.of(_01_30),
            Arguments.of(_02_00),
            Arguments.of(_02_30),
            Arguments.of(_03_00),
            Arguments.of(_03_30),
            Arguments.of(_04_00),
            Arguments.of(_04_30),
            Arguments.of(_05_00),
            Arguments.of(_05_30),
            Arguments.of(_06_00),
            Arguments.of(_06_30),
            Arguments.of(_07_00),
            Arguments.of(_07_30),
            Arguments.of(_08_00),
            Arguments.of(_08_30),
            Arguments.of(_09_00),
            Arguments.of(_09_30),
            Arguments.of(_10_00),
            Arguments.of(_10_30)
        );
    }

    private static Stream<Arguments> provideLocalTimeBetweenStartAndEndTimeWhenEndTimeIsZero() {
        return Stream.of(
            Arguments.of(_00_00),
            Arguments.of(_00_30),
            Arguments.of(_01_00),
            Arguments.of(_01_30),
            Arguments.of(_02_00),
            Arguments.of(_02_30),
            Arguments.of(_03_00),
            Arguments.of(_03_30),
            Arguments.of(_04_00),
            Arguments.of(_04_30),
            Arguments.of(_05_00),
            Arguments.of(_05_30),
            Arguments.of(_06_00),
            Arguments.of(_06_30),
            Arguments.of(_07_00),
            Arguments.of(_07_30),
            Arguments.of(_08_00),
            Arguments.of(_08_30),
            Arguments.of(_09_00),
            Arguments.of(_09_30),
            Arguments.of(_10_00),
            Arguments.of(_10_30),
            Arguments.of(_11_00),
            Arguments.of(_11_30),
            Arguments.of(_12_00),
            Arguments.of(_12_30),
            Arguments.of(_13_00),
            Arguments.of(_13_30),
            Arguments.of(_14_00),
            Arguments.of(_14_30),
            Arguments.of(_15_00),
            Arguments.of(_15_30),
            Arguments.of(_16_00),
            Arguments.of(_16_30),
            Arguments.of(_17_00),
            Arguments.of(_17_30),
            Arguments.of(_18_00),
            Arguments.of(_18_30),
            Arguments.of(_19_00),
            Arguments.of(_19_30),
            Arguments.of(_20_00),
            Arguments.of(_20_30),
            Arguments.of(_21_00),
            Arguments.of(_21_30),
            Arguments.of(_22_00),
            Arguments.of(_22_30)
        );
    }

    private static Stream<Arguments> provideLocalTimeNotBetweenStartAndEndTimeWhenEndTimeIsZero() {
        return Stream.of(
            Arguments.of(_23_00),
            Arguments.of(_23_30)
        );
    }

    private static Stream<Arguments> provideAllTime() {
        return Stream.of(
            Arguments.of(_00_00),
            Arguments.of(_00_30),
            Arguments.of(_01_00),
            Arguments.of(_01_30),
            Arguments.of(_02_00),
            Arguments.of(_02_30),
            Arguments.of(_03_00),
            Arguments.of(_03_30),
            Arguments.of(_04_00),
            Arguments.of(_04_30),
            Arguments.of(_05_00),
            Arguments.of(_05_30),
            Arguments.of(_06_00),
            Arguments.of(_06_30),
            Arguments.of(_07_00),
            Arguments.of(_07_30),
            Arguments.of(_08_00),
            Arguments.of(_08_30),
            Arguments.of(_09_00),
            Arguments.of(_09_30),
            Arguments.of(_10_00),
            Arguments.of(_10_30),
            Arguments.of(_11_00),
            Arguments.of(_11_30),
            Arguments.of(_12_00),
            Arguments.of(_12_30),
            Arguments.of(_13_00),
            Arguments.of(_13_30),
            Arguments.of(_14_00),
            Arguments.of(_14_30),
            Arguments.of(_15_00),
            Arguments.of(_15_30),
            Arguments.of(_16_00),
            Arguments.of(_16_30),
            Arguments.of(_17_00),
            Arguments.of(_17_30),
            Arguments.of(_18_00),
            Arguments.of(_18_30),
            Arguments.of(_19_00),
            Arguments.of(_19_30),
            Arguments.of(_20_00),
            Arguments.of(_20_30),
            Arguments.of(_21_00),
            Arguments.of(_21_30),
            Arguments.of(_22_00),
            Arguments.of(_22_30),
            Arguments.of(_23_00),
            Arguments.of(_23_30)
        );
    }
}

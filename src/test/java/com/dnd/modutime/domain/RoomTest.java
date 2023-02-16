package com.dnd.modutime.domain;

import static com.dnd.modutime.domain.fixture.RoomFixture.getRoom;
import static com.dnd.modutime.domain.fixture.RoomFixture.getRoomByHeadCount;
import static com.dnd.modutime.domain.fixture.RoomFixture.getRoomByStartEndTime;
import static com.dnd.modutime.domain.fixture.RoomFixture.getRoomByTitle;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10_00_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_20_00_00;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.dnd.modutime.domain.room.Room;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RoomTest {

    @Test
    void 방이_생성되면_Uuid를_생성한다() {
        Room room = getRoom();
        assertThat(room.getUuid()).isNotNull();
    }

    @Test
    void 방의_시작시간이_끝나는시간보다_나중이면_예외가_발생한다() {
        assertThatThrownBy(() -> getRoomByStartEndTime(_13_00, _12_00))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 날짜가_비어있으면_예외가_발생한다() {
        List<LocalDate> dates = List.of();
        assertThatThrownBy(() -> getRoom(dates))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 날짜가_null_이면_예외가_발생한다() {
        assertThatThrownBy(() -> getRoom(null))
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
}

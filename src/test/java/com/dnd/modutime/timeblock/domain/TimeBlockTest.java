package com.dnd.modutime.timeblock.domain;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class TimeBlockTest {

    @Test
    void TimeBlock_생성시_roomUuid가_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getTimeBlock(ROOM_UUID, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void TimeBlock_생성시_participantName이_null이면_예외가_발생한다() {
        assertThatThrownBy(() -> getTimeBlock(null, "참여자1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void TimeBlock_생성시_가능한시간들이_빈리스트로_초기화된다() {
        TimeBlock timeBlock = getTimeBlock(ROOM_UUID, "참여자1");
        assertThat(timeBlock.getAvailableDateTimes()).isEmpty();
    }

    private TimeBlock getTimeBlock(String roomUuid,
                                   String participantName) {
        return new TimeBlock(roomUuid, participantName);
    }
}


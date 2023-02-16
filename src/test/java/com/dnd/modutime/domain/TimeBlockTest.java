package com.dnd.modutime.domain;

import static com.dnd.modutime.fixture.RoomFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import com.dnd.modutime.domain.timeblock.AvailableDateTimeValidator;
import com.dnd.modutime.domain.timeblock.TimeBlock;
import java.util.List;
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

    @Test
    void TimeBlock의_가능한시간_교체시_Validator에서_예외가_발생하면_예외가_발생해야한다() {
        TimeBlock timeBlock = getTimeBlock((it1, ti2) -> {throw new IllegalArgumentException();});
        assertThatThrownBy(() -> timeBlock.replace(List.of(new AvailableDateTime(_2023_02_10, List.of(_12_00)))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private TimeBlock getTimeBlock(String roomUuid,
                                   String participantName) {
        return new TimeBlock(roomUuid, participantName, (it1, it2) -> {});
    }

    private TimeBlock getTimeBlock(AvailableDateTimeValidator availableDateTimeValidator) {
        return new TimeBlock(ROOM_UUID, "참여자1", availableDateTimeValidator);
    }
}


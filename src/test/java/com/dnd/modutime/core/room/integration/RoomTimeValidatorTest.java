package com.dnd.modutime.core.room.integration;

import static com.dnd.modutime.fixture.RoomFixture.getRoomByDeadLine;
import static com.dnd.modutime.fixture.RoomFixture.getRoomByStartEndTime;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_09_00_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_20_00_00;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.dnd.modutime.config.TimeConfiguration;
import com.dnd.modutime.core.room.application.RoomTimeValidator;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.util.FakeTimeProvider;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TimeConfiguration.class)
@SpringBootTest
class RoomTimeValidatorTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTimeValidator roomTimeValidator;

    @Autowired
    private FakeTimeProvider timeProvider;

    @AfterEach
    void cleanUp() {
        timeProvider.setTime(_2023_02_09_00_00);
    }

    @Test
    void 시작과_끝나는_시간이_있는_방에_시간값을_넘겨주지_않으면_예외가_발생한다() {
        Room room = getRoomByStartEndTime(_12_00, _13_00);
        Room savedRoom = roomRepository.save(room);
        List<AvailableDateTime> availableDateTimes = List.of(new AvailableDateTime(new TimeBlock(savedRoom.getUuid(),
                "참여자1"), _2023_02_10, null));
        assertThatThrownBy(() -> roomTimeValidator.validate(savedRoom.getUuid(), availableDateTimes))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 시작과_끝나는_시간이_없는_방에_시간값을_넘겨주면_예외가_발생한다() {
        Room room = getRoomByStartEndTime(null, null);
        Room savedRoom = roomRepository.save(room);
        List<AvailableDateTime> availableDateTimes = List.of(new AvailableDateTime(new TimeBlock(savedRoom.getUuid(),
                "참여자1"), _2023_02_10, List.of(new AvailableTime(_12_00))));
        assertThatThrownBy(() -> roomTimeValidator.validate(savedRoom.getUuid(), availableDateTimes))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 시작과_끝나는_시간이_없는_방에_시간값을_넘겨주지_않으면_예외가_발생하지_않는다() {
        Room room = getRoomByStartEndTime(null, null);
        Room savedRoom = roomRepository.save(room);
        List<AvailableDateTime> availableDateTimes = List.of(new AvailableDateTime(new TimeBlock(savedRoom.getUuid(),
                "참여자1"), _2023_02_10, null));
        assertDoesNotThrow(() -> roomTimeValidator.validate(savedRoom.getUuid(), availableDateTimes));
    }

    @Test
    void 현재시간이_방의_데드라인이후인경우_예외가_발생한다() {
        Room room = getRoomByStartEndTime(_12_00, _13_00);
        Room savedRoom = roomRepository.save(room);
        List<AvailableDateTime> availableDateTimes = List.of(new AvailableDateTime(new TimeBlock(savedRoom.getUuid(),
                "참여자1"), _2023_02_10, List.of(new AvailableTime(_12_00))));
        timeProvider.setTime(_2023_02_20_00_00);
        assertThatThrownBy(() -> roomTimeValidator.validate(savedRoom.getUuid(), availableDateTimes))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 데드라인이_없는_방인경우_데드라인_예외가_발생하지_않는다() {
        Room room = getRoomByDeadLine(null);
        Room savedRoom = roomRepository.save(room);
        List<AvailableDateTime> availableDateTimes = List.of(new AvailableDateTime(new TimeBlock(savedRoom.getUuid(),
                "참여자1"), _2023_02_10, List.of(new AvailableTime(_12_00))));
        timeProvider.setTime(_2023_02_20_00_00);
        assertDoesNotThrow(() -> roomTimeValidator.validate(savedRoom.getUuid(), availableDateTimes));
    }
}

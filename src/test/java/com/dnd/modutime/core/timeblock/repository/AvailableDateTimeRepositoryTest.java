package com.dnd.modutime.core.timeblock.repository;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timeblock.repository.AvailableDateTimeRepository;
import com.dnd.modutime.core.timeblock.repository.TimeBlockRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AvailableDateTimeRepositoryTest {

    @Autowired
    private AvailableDateTimeRepository availableDateTimeRepository;

    @Autowired
    private TimeBlockRepository timeBlockRepository;

    @Test
    void AvailableDateTime을_조회하면_times도_같이_조회해온다() {
        TimeBlock savedTimeBlock = timeBlockRepository.save(new TimeBlock(ROOM_UUID, "참여자1"));
        AvailableDateTime availableDateTime = new AvailableDateTime(savedTimeBlock, _2023_02_10,
                List.of(
                        new AvailableTime(_12_00),
                        new AvailableTime(_13_00))
        );
        availableDateTimeRepository.save(availableDateTime);

        final List<AvailableDateTime> byTimeBlockId = availableDateTimeRepository.findByTimeBlockId(savedTimeBlock.getId());
        int a = 0;
    }
}
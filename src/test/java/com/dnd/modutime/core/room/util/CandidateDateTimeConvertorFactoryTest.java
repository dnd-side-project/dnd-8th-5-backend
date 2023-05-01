package com.dnd.modutime.core.room.util;

import static com.dnd.modutime.fixture.RoomFixture.getRoom;
import static com.dnd.modutime.fixture.RoomFixture.getRoomByStartEndTime;
import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.dnd.modutime.core.adjustresult.util.convertor.CandidateDateTimeConvertor;
import com.dnd.modutime.core.adjustresult.util.convertor.DateRoomConvertor;
import com.dnd.modutime.core.adjustresult.util.convertor.DateTimeRoomConvertor;
import com.dnd.modutime.core.room.repository.RoomRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CandidateDateTimeConvertorFactoryTest {

    @Autowired
    private CandidateDateTimeConvertorFactory candidateDateTimeConvertorFactory;

    @MockBean
    private RoomRepository roomRepository;

    @Test
    void 시간이없는_방이라면_dateRoomConvertor_을_반환한다() {
        given(roomRepository.findByUuid(ROOM_UUID)).willReturn(
                Optional.of(getRoomByStartEndTime(null, null))
        );
        CandidateDateTimeConvertor convertor = candidateDateTimeConvertorFactory.getInstance(ROOM_UUID);
        assertThat(convertor).isInstanceOf(DateRoomConvertor.class);
    }

    @Test
    void 시간이_포함된_방이라면_dateTimeRoomConvertor_을_반환한다() {
        given(roomRepository.findByUuid(ROOM_UUID)).willReturn(
                Optional.of(getRoom())
        );
        CandidateDateTimeConvertor convertor = candidateDateTimeConvertorFactory.getInstance(ROOM_UUID);
        assertThat(convertor).isInstanceOf(DateTimeRoomConvertor.class);
    }
}

package com.dnd.modutime.adjustresult.domain;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class AdjustmentResultTest {

    @Test
    void dateInfos정보로_AdjustmentResult를_생성한다() {
//        AdjustmentResult adjustmentResult = AdjustmentResult.of(ROOM_UUID, List.of(
//                getDateInfo(_2023_02_09, List.of(getTimeInfo(_12_00, List.of()), getTimeInfo(_12_30, List.of()),
//                        getTimeInfo(_13_00, List.of()))),
//                getDateInfo(_2023_02_10, List.of(getTimeInfo(_12_30, List.of()), getTimeInfo(_13_00, List.of()),
//                        getTimeInfo(_13_30, List.of()))
//                )));
//
    }
    @Test
    void 생성시_확정상태_false_로_생성된다() {
        AdjustmentResult adjustmentResult = new AdjustmentResult(ROOM_UUID, List.of());
        assertThat(adjustmentResult.isConfirmation()).isFalse();
    }

//    private TimeInfo getTimeInfo(LocalTime time, List<String> participantNames) {
//        List<TimeTableParticipantName> timeTableParticipantNames = participantNames.stream()
//                .map(participantName -> new TimeTableParticipantName(getTimeInfo(), participantName))
//                .collect(Collectors.toList());
//        return new TimeInfo(time, timeTableParticipantNames);
//    }
//
//    private TimeInfo getTimeInfo() {
//        return new TimeInfo(null, null);
//    }
}
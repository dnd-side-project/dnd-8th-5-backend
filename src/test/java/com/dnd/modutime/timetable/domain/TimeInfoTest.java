package com.dnd.modutime.timetable.domain;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class TimeInfoTest {

    @Test
    void time이_같으면_해당_참여자_이름을_지운다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");

        timeInfo.removeParticipantNameIfSameTime(_12_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).doesNotContain("참여자1");
    }

    @Test
    void time이_같지_않으면_해당_참여자_이름을_지우지_않는다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");

        timeInfo.removeParticipantNameIfSameTime(_13_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).contains("참여자1");
    }

    @Test
    void time이_같으면_해당_참여자_이름을_추가한다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).contains("참여자1");
    }

    @Test
    void time이_같지_않으면_해당_참여자_이름을_추가하지_않는다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_13_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).doesNotContain("참여자1");
    }

    @Test
    void 이미_가지고있는_참여자는_추가되지_않는다() {
        TimeInfo timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");

        timeInfo.addParticipantName("참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames()).hasSize(1);
    }
}

package com.dnd.modutime.core.timetable.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._13_00;
import static org.assertj.core.api.Assertions.assertThat;

public class TimeInfoTest {

    @Test
    void time이_같으면_해당_참여자_이름을_지운다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");

        timeInfo.removeParticipantNameIfSameTime(_12_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).doesNotContain("참여자1");
    }

    @Test
    void time이_같지_않으면_해당_참여자_이름을_지우지_않는다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");

        timeInfo.removeParticipantNameIfSameTime(_13_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).contains("참여자1");
    }

    @Test
    void time이_같으면_해당_참여자_이름을_추가한다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).contains("참여자1");
    }

    @Test
    void time이_같지_않으면_해당_참여자_이름을_추가하지_않는다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_13_00, "참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames().stream()
                .map(TimeInfoParticipantName::getName)
                .collect(Collectors.toList())).doesNotContain("참여자1");
    }

    @Test
    void 이미_가지고있는_참여자는_추가되지_않는다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantNameIfSameTime(_12_00, "참여자1");

        timeInfo.addParticipantName("참여자1");
        assertThat(timeInfo.getTimeInfoParticipantNames()).hasSize(1);
    }

    @Test
    void 참여자_목록에_포함된_참여자가_있으면_true를_반환한다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantName("참여자1");
        timeInfo.addParticipantName("참여자2");

        var participantNames = List.of("참여자1", "참여자3");

        assertThat(timeInfo.hasAnyParticipant(participantNames)).isTrue();
    }

    @Test
    void 참여자_목록에_포함된_참여자가_없으면_false를_반환한다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantName("참여자1");
        timeInfo.addParticipantName("참여자2");

        var participantNames = List.of("참여자3", "참여자4");

        assertThat(timeInfo.hasAnyParticipant(participantNames)).isFalse();
    }

    @Test
    void 참여자_목록이_null이면_false를_반환한다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantName("참여자1");

        assertThat(timeInfo.hasAnyParticipant(null)).isFalse();
    }

    @Test
    void 참여자_목록이_비어있으면_false를_반환한다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        timeInfo.addParticipantName("참여자1");

        assertThat(timeInfo.hasAnyParticipant(List.of())).isFalse();
    }

    @Test
    void TimeInfo에_참여자가_없으면_false를_반환한다() {
        var timeInfo = new TimeInfo(_12_00, new ArrayList<>());
        var participantNames = List.of("참여자1", "참여자2");

        assertThat(timeInfo.hasAnyParticipant(participantNames)).isFalse();
    }
}

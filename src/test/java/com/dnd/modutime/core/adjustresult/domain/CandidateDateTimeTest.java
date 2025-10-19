
package com.dnd.modutime.core.adjustresult.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.core.participant.domain.Participant;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CandidateDateTimeTest {

    @DisplayName("참여자가 모두 동일한지 확인한다")
    @Test
    void test01() {
        // given
        var candidate = new CandidateDateTime(
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                List.of(
                        new CandidateDateTimeParticipantName("alice"),
                        new CandidateDateTimeParticipantName("bob"),
                        new CandidateDateTimeParticipantName("carol")
                )
        );

        var participants = List.of(
                new Participant("room-1", "carol", "1234"),
                new Participant("room-1", "alice", "1234"),
                new Participant("room-1", "bob", "1234")
        );

        // when
        boolean result = candidate.containsExactly(participants);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("참여자가 한명이라도 다르면 false를 반환한다")
    @Test
    void test02() {
        // given
        var candidate = new CandidateDateTime(
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                false,
                List.of(
                        new CandidateDateTimeParticipantName("alice"),
                        new CandidateDateTimeParticipantName("bob"),
                        new CandidateDateTimeParticipantName("carol")
                )
        );

        var participants = List.of(
                new Participant("room-1", "carol", "1234"),
                new Participant("room-1", "alice", "1234")
        );

        // when
        boolean result = candidate.containsExactly(participants);

        // then
        assertThat(result).isFalse();
    }
}
